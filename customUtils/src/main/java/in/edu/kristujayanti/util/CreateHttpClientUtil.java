package in.edu.kristujayanti.util;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

public class CreateHttpClientUtil {
    //  Send JSON POST request
    public static Future<JsonObject> sendJsonPostRequest(WebClient client, String uri, String root, JsonObject payload, int port) {
        return client.post(port, uri, root)
                .sendJsonObject(payload)
                .map(res -> {
                    // Process the successful response
                    JsonObject responseData = new JsonObject();
                    responseData.put("data", res.body().toJson());
                    return responseData;
                })
                .recover(err -> {
                    // Handle errors
                    System.err.println("Failed to send POST request: " + err.getMessage());
                    JsonObject errorResponse = new JsonObject().put("error", err.getMessage());
                    return (Future<JsonObject>) errorResponse;
                });
    }


    //Send Form POST request
    public static Future<JsonObject> sendFormPostRequest(WebClient client, String uri, String root, MultiMap payload, int port) {
        return client.post(port, uri, root)
                .putHeader("content-type", "multipart/form-data")
                .sendForm(payload)
                .map(res -> {
                    JsonObject responseData = new JsonObject();
                    responseData.put("futureResponse", res.bodyAsString());
                    return responseData;
                })
                .recover(err -> {
                    System.err.println("Failed to send POST request: " + err.getMessage());
                    JsonObject errorResponse = new JsonObject().put("error", err.getMessage());
                    return (Future<JsonObject>) errorResponse;
                });
    }
}