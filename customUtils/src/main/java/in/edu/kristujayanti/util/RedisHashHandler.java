package in.edu.kristujayanti.util;

import io.vertx.redis.client.Command;
import io.vertx.redis.client.Request;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.Response;

import java.util.HashMap;
import java.util.Map;

public class RedisHashHandler {

    public static void setFieldValuePairs(Redis redisClient, String key, Map<String, String> fieldValuePairs) {

            fieldValuePairs.forEach((field, value) -> {
                // Create a request for the HSET command
                Request request = Request.cmd(Command.HSET).arg(key).arg(field).arg(value);

                // Send the request to Redis
                redisClient.send(request, ar -> {
                    if (ar.succeeded()) {
                        System.out.println("Value successfully stored for field " + field + " under key " + key);
                    } else {
                        System.err.println("Failed to store value for field " + field + " under key " + key + ": " + ar.cause().getMessage());
                    }
                });
            });
    }

    public static Map<String, String> getFieldValues(Redis redisClient, String key, Map<String, String> fields) {
        Map<String, String> result = new HashMap<>(fields); // Copy the input map to avoid modifying it

        for (Map.Entry<String, String> entry : fields.entrySet()) {
            String field = entry.getKey();

            // Create a request for the HGET command
            Request request = Request.cmd(Command.HGET).arg(key).arg(field);

            // Send the request to Redis
            redisClient.send(request, ar -> {
                if (ar.succeeded()) {
                    Response response = ar.result();
                    String fetchedValue = response.toString(); // Assuming the response is directly convertible to the value string
                    result.put(field, fetchedValue);
                    System.out.println("Fetched value for field " + field + " under key " + key + ": " + fetchedValue);
                } else {
                    System.err.println("Failed to retrieve value for field " + field + " under key " + key + ": " + ar.cause().getMessage());
                }
            });
        }

        return result;
    }
}
