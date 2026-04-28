package in.edu.kristujayanti.util;

import in.edu.kristujayanti.enums.ResponseType;
import in.edu.kristujayanti.enums.StatusCode;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

public class RequestValidatorUtil {

    private RequestValidatorUtil() {}

    public static boolean validateRequest(RoutingContext context, List<CharSequence> requiredHeaders, List<String> requiredKeys) {

        // VALIDATE HEADERS
        MultiMap headers = context.request().headers();
        for (CharSequence header : requiredHeaders) {
            String requiredHeader = headers.get(header);
            if ((requiredHeader == null) || requiredHeader.isEmpty()) {
                respondWithError(context, header + " header is missing.");
                return false;
            }
        }

        // VALIDATE BODY
        if (!requiredKeys.isEmpty()){

            String contentType = headers.get(HttpHeaders.CONTENT_TYPE);
            if (contentType == null || !contentType.contains("application/json")) {
                respondWithError(context, "Content type must be application/json.");
                return false;
            }

            JsonObject body = context.body().asJsonObject();
            if (body == null) {
                respondWithError(context, "JSON body is missing.");
                return false;
            }

            for (String key : requiredKeys) {
                if (!body.containsKey(key)) {
                    respondWithError(context, "Required key '" + key + "' is missing in the JSON body.");
                    return false;
                }
            }
        }

        return true;
    }

    private static void respondWithError(RoutingContext context, String errorMessage) {
        ResponseUtil.createResponse(context.response(), ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(), new JsonArray().add(errorMessage));
    }
}
