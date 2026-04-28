package in.edu.kristujayanti.router;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.redis.client.Redis;

/**
 * Abstract base class for sub-router builders.
 * Provides common properties and constructor for subclasses to initialize necessary components.
 */
public abstract class SubRouterBuilderBase implements RouterBuilderService {

    // Protected members accessible by subclasses
    protected final Router router;                   // Vert.x Router instance for handling routes
    protected final Redis redisCommandConnection;    // Redis connection for command operations
    protected final MongoDatabase mongoDatabase;     // MongoDB database instance
    protected final MongoClient mongoClient;         // MongoDB client instance
    protected final JsonObject apiInfo;              // JSON object containing API information
    protected final WebClient webClient;             // Vert.x WebClient for making HTTP requests
    protected final Vertx vertx;                     // Vertx instance

    /**
     * Constructor to initialize the common properties for subclasses.
     *
     * @param router                The Vert.x Router instance.
     * @param redisCommandConnection The Redis connection for command operations.
     * @param mongoDatabase         The MongoDB database instance.
     * @param mongoClient           The MongoDB client instance.
     * @param apiInfo               JSON object containing API information.
     * @param webClient             The Vert.x WebClient for making HTTP requests.
     * @param vertx                 The Vertx instance.
     */
    SubRouterBuilderBase(Router router, Redis redisCommandConnection, MongoDatabase mongoDatabase, MongoClient mongoClient,
                                JsonObject apiInfo, WebClient webClient, Vertx vertx) {
        this.router = router;
        this.redisCommandConnection = redisCommandConnection;
        this.mongoDatabase = mongoDatabase;
        this.mongoClient = mongoClient;
        this.apiInfo = apiInfo;
        this.webClient = webClient;
        this.vertx = vertx;
    }
}
