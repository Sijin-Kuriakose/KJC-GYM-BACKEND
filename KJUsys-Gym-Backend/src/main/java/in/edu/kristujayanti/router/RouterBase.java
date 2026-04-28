package in.edu.kristujayanti.router;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.redis.client.Redis;

// Abstract base class for defining common properties and initialization for router classes
public abstract class RouterBase {

    // Protected members accessible by subclasses
    protected final Router router;                   // Vert.x Router instance for handling routes
    protected final Redis redisCommandConnection;
    protected  final  String redisHashkey;// Redis connection for command operations
    protected final MongoDatabase mongoDatabase;     // MongoDB database instance
    protected final MongoClient mongoClient;         // MongoDB client instance
    protected final WebClient client;                // Vert.x WebClient for making HTTP requests

    // Constructor to initialize the common properties for subclasses
    RouterBase(Router router, Redis redisCommandConnection,String redisHashkey, MongoDatabase mongoDatabase, MongoClient mongoClient, WebClient client) {
        this.router = router;
        this.redisCommandConnection = redisCommandConnection;
        this.redisHashkey=redisHashkey;
        this.mongoDatabase = mongoDatabase;
        this.mongoClient = mongoClient;
        this.client = client;
    }
}
