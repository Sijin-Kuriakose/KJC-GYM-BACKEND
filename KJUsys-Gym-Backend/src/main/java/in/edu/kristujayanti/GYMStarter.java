package in.edu.kristujayanti;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import in.edu.kristujayanti.exception.BootstrapException;
import in.edu.kristujayanti.router.GYMRouter;
import in.edu.kristujayanti.router.RouterBuilder;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisOptions;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class GYMStarter {
    // Logger instance for logging information
    private static final Logger LOGGER = LoggerFactory.getLogger(GYMStarter.class);

    // Vert.x instance, HTTP server, and router initialization
    private final Vertx vertx = Vertx.vertx();
    private final HttpServer server = vertx.createHttpServer();
    private final Router router = Router.router(vertx);
    private Redis redisCommandConnection; // Redis connection

    // List of valid build environments
    private static final List<String> BUILD_ENVIRONMENTS = Arrays.asList("development", "CI", "demo", "production");

    // Configuration keys
    private static final String REDIS_CONNECT_INFO = "redis-connect-info";
    private static final String MONGO_CONNECT_INFO = "mongo-connect-info";
    private static final String KJUSYS_GYM_APP_INFO = "gym-app-info";
    private static final String DB_NAME = "db_name";
    private static final String CONNECT_URL = "connectURL";
    private static final String APP_CONFIG_VALUES_TEXT = "app-config-values";
    private String currentEnvironment;

    // Main method to start the application
    public static void main(String[] args) {
        try {
            // Parse command-line arguments
            CommandLine commandParser = new DefaultParser().parse(getOptions(), args);
            String configServerURL = commandParser.getOptionValue("config_server_address");
            String environment = commandParser.getOptionValue("environment");

            LOGGER.info("Using configuration server(redis) cluster {} for bootstrapping on {} environment.", configServerURL, environment);

            // Create GYMStarter instance and start bootstrapping
            GYMStarter gymStarter = new GYMStarter();
            gymStarter.bootstrapApplication(configServerURL, environment);
        } catch (ParseException e) {
            LOGGER.error("Invalid command line arguments. Expected format: -config_server_address IP:PORT -environment buildMode.", e);
            System.exit(0);
        } catch (BootstrapException e) {
            LOGGER.error(e.getMessage());
            System.exit(0);
        }
    }

    // Method to define command-line options
    private static Options getOptions() {
        Options options = new Options();
        options.addOption("config_server_address", true, "Configuration server IP & port");
        options.addOption("environment", true, "Environment development|CI|demo|production");
        return options;
    }

    // Method to bootstrap the application with given configuration server URL and environment
    private void bootstrapApplication(String configServerURL, String environment) {
        validateConfigServerURL(configServerURL); // Validate the config server URL format
        validateEnvironment(environment); // Validate the environment

        // Key for application configuration based on the environment
        String appConfigKey = "app-config-values-" + environment;
        // Create a ConfigRetriever to fetch the configuration from the Redis server
        ConfigRetriever configRetriever = createConfigRetriever(configServerURL, appConfigKey);

        // Retrieve the configuration and handle the result
        configRetriever.getConfig(this::handleConfigRetrieval);
    }



    // Method to validate the configuration server URL
    private void validateConfigServerURL(String configServerURL) {
        if (configServerURL.split(":").length != 2) {
            throw new BootstrapException("Invalid format of configuration server URL. Expected format: IP:Port.");
        }
    }

    // Method to validate the environment value
    private void validateEnvironment(String environment) {
        if (!BUILD_ENVIRONMENTS.contains(environment)) {
            throw new BootstrapException("Invalid environment value. Expected values are: " + String.join("|", BUILD_ENVIRONMENTS));
        }
        this.currentEnvironment = environment;

    }

    // Method to create a ConfigRetriever for fetching configuration from the Redis server
    private ConfigRetriever createConfigRetriever(String configServerURL, String appConfigKey) {
        String[] tokens = configServerURL.split(":");
        ConfigStoreOptions storeOptions = new ConfigStoreOptions().setType("redis")
                .setConfig(new JsonObject().put("endpoints", new JsonArray().add("redis://" + tokens[0] + ":" + tokens[1]))
                        .put("key", appConfigKey));
        return ConfigRetriever.create(vertx, new ConfigRetrieverOptions().addStore(storeOptions));
    }

    // Method to handle the configuration retrieval result
    private void handleConfigRetrieval(AsyncResult<JsonObject> asyncResult) {
        if (asyncResult.failed()) {
            LOGGER.error("Failed to retrieve config from server.", asyncResult.cause());
            System.exit(0);
        }

        JsonObject config = asyncResult.result();
        if (config == null) {
            LOGGER.error("Configuration from config server is null.");
            System.exit(0);
        }

        // Setup database connections, routers, services, and HTTP server
        setupDatabaseConnections(config);
        setupRoutersAndServices(config);
        setupHttpServer(config);
    }

    // Method to set up database connections (Redis and MongoDB)
    private void setupDatabaseConnections(JsonObject config) {
        if (!config.containsKey(REDIS_CONNECT_INFO)) {
            LOGGER.error("Redis connect info missing from configuration server.");
            System.exit(0);
        }

        // Setup Redis connection
        JsonObject redisConfig = new JsonObject(config.getString(REDIS_CONNECT_INFO));
        RedisOptions redisOptions = createRedisOptions(redisConfig);
        redisCommandConnection = Redis.createClient(vertx, redisOptions);

        redisCommandConnection.connect(res -> {
            if (res.succeeded()) {
                LOGGER.info("Redis connection established.");
            } else {
                LOGGER.error("Failed to establish Redis connection.", res.cause());
                System.exit(0);
            }
        });

        // Check if MongoDB connection info is present
        if (!config.containsKey(MONGO_CONNECT_INFO)) {
            LOGGER.error("MongoDB connect info missing from configuration server.");
            System.exit(0);
        }
    }

    // Method to set up routers and services
    private void setupRoutersAndServices(JsonObject config) {
        JsonObject mongoConfig = new JsonObject(config.getString(MONGO_CONNECT_INFO));
        MongoClient mongoClient = getMongoClient(mongoConfig);
        MongoDatabase mongoDatabase = getMongoDatabase(mongoConfig, mongoClient);
        WebClient webClient = WebClient.create(vertx);
        String redisHashKey = "%s-%s".formatted(APP_CONFIG_VALUES_TEXT, currentEnvironment);
        // Check if gym app info is present
        if (!config.containsKey(KJUSYS_GYM_APP_INFO)) {
            LOGGER.error("KJUsys gym app info missing from configuration server.");
            System.exit(0);
        }


        JsonObject apiInfo = new JsonObject(config.getString(KJUSYS_GYM_APP_INFO));
        LOGGER.info("Setting up routers.");
        try {
            // Initialize routers with the required configurations
            RouterBuilder routerBuilder = new RouterBuilder();
            routerBuilder.initialize(router, redisCommandConnection, mongoDatabase, mongoClient, apiInfo, webClient, vertx);

            // Setup gym routers
            GYMRouter gymRouter = new GYMRouter(router, redisCommandConnection,redisHashKey, mongoDatabase, mongoClient, webClient, vertx);
            gymRouter.setUpRouters();
        } catch (Exception e) {
            LOGGER.error("Error initializing routers.", e);
            System.exit(0);
        }
    }

    // Method to set up the HTTP server
    private void setupHttpServer(JsonObject config) {
        JsonObject apiInfo = new JsonObject(config.getString(KJUSYS_GYM_APP_INFO));

        LOGGER.info("Setting up HTTP server.");
        server.requestHandler(router).listen(apiInfo.getInteger("port"), res -> {
            if (res.succeeded()) {
                LOGGER.info("Bootstrap complete. {} listening on port {}.", apiInfo.getString("api_name"), apiInfo.getInteger("port"));
            } else {
                LOGGER.error("Failed to start HTTP server.", res.cause());
                System.exit(0);
            }
        });

        // Shutdown hook to clean up resources on application exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutting down GYM application.");
            vertx.close();
            redisCommandConnection.close();
            server.close();
        }));
    }

    // Method to create Redis options from the configuration
    private RedisOptions createRedisOptions(JsonObject redisConfig) {
        if (!redisConfig.containsKey(CONNECT_URL) || redisConfig.getJsonArray(CONNECT_URL).isEmpty()) {
            LOGGER.error("Redis connect URL missing from configuration server.");
            System.exit(0);
        }

        RedisOptions redisOptions = new RedisOptions();
        redisConfig.getJsonArray(CONNECT_URL).forEach(url -> redisOptions.addConnectionString(url.toString()));
        return redisOptions;
    }

    // Method to get MongoDB client from the configuration
    private MongoClient getMongoClient(JsonObject mongoConfig) {
        if (!mongoConfig.containsKey("connection_string") || !mongoConfig.containsKey(DB_NAME)) {
            LOGGER.error("MongoDB connection string or DB name missing from configuration server.");
            System.exit(0);
        }

        String connectionString = mongoConfig.getString("connection_string") + "/" + mongoConfig.getString(DB_NAME);
        return MongoClients.create(connectionString);
    }

    // Method to get MongoDB database instance from the configuration
    private MongoDatabase getMongoDatabase(JsonObject mongoConfig, MongoClient mongoClient) {
        return mongoClient.getDatabase(mongoConfig.getString(DB_NAME));
    }
}