package in.edu.kristujayanti.router;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.redis.client.Redis;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public class RouterBuilder {

    /**
     * Initializes the sub-routers and attaches them to the parent router.
     *
     * @param parentRouter          The main router to which sub-routers will be attached.
     * @param redisCommandConnection Redis connection for command operations.
     * @param mongoDatabase         MongoDB database instance.
     * @param mongoClient           MongoDB client instance.
     * @param apiInfo               JSON object containing API information.
     * @param webClient             WebClient for making HTTP requests.
     * @param vertx                 Vertx instance.
     * @throws NoSuchMethodException     If a method required for reflection is not found.
     * @throws InvocationTargetException If an error occurs while invoking a method via reflection.
     * @throws InstantiationException    If an error occurs while creating an instance of a sub-router class.
     * @throws IllegalAccessException    If there is illegal access while invoking a method via reflection.
     */
    public void initialize(Router parentRouter, Redis redisCommandConnection, MongoDatabase mongoDatabase, MongoClient mongoClient, JsonObject apiInfo, WebClient webClient,
                           Vertx vertx) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        // Retrieve the set of sub-router classes
        Set<Class<? extends SubRouterBuilderBase>> subRouterClasses = SubRouterBuilders.getSubRouterClasses();

        // Iterate over each sub-router class
        for (Class<?> subRouterClass : subRouterClasses) {
            // Get the constructor that takes the required parameters
            Constructor<?> constructor = subRouterClass.getConstructor(Router.class, Redis.class, MongoDatabase.class, MongoClient.class, JsonObject.class, WebClient.class, Vertx.class);

            // Create an instance of the sub-router class using the constructor
            Object instance = constructor.newInstance(parentRouter, redisCommandConnection, mongoDatabase, mongoClient, apiInfo, webClient, vertx);

            // Get the createRouter method
            Method createRouterMethod = subRouterClass.getMethod("createRouter");
            // Invoke the createRouter method and get the sub-router
            Router subRouter = (Router) createRouterMethod.invoke(instance);

            // Get the getPath method
            Method getPathMethod = subRouterClass.getMethod("getPath");
            // Invoke the getPath method to get the path for the sub-router
            String path = (String) getPathMethod.invoke(instance);

            // Attach the sub-router to the parent router at the specified path
            parentRouter.route(path).subRouter(subRouter);
        }
    }
}
