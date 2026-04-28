package in.edu.kristujayanti.util;

import com.auth0.jwt.JWT;
import in.edu.kristujayanti.constants.AuthConstantKeys;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;

import java.util.Collections;
import java.util.List;

import static in.edu.kristujayanti.propertyBinder.KJUSYSCommonKeysPBinder.*;

public class UserInfoUtil {

    // Private constructor to prevent instantiation
    private UserInfoUtil() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    /**
     * Retrieves the logged-in user's id from the authorization header.
     *
     * @param headers the HTTP headers containing the authorization token
     * @return the user ID extracted from the JWT token, or an empty string if not found
     */
    public static String getLoggedInUserId(MultiMap headers) {
        return fetchClaimFromJWT(headers, AuthConstantKeys.USER_ID);
    }

    /**
     * Retrieves the logged-in user's email from the authorization header.
     *
     * @param headers the HTTP headers containing the authorization token
     * @return the user Email extracted from the JWT token, or an empty string if not found
     */
    public static String getLoggedInUserEmail(MultiMap headers) {
        return fetchClaimFromJWT(headers, AuthConstantKeys.EMAIL);
    }

    /**
     * Extracts a specified claim from a JSON Web Token (JWT) found in the
     * Authorization header of the provided headers.
     *
     * @param headers A MultiMap containing the HTTP headers, which should
     *                include the Authorization header with a (Bearer) token.
     * @param claim The name of the claim to extract from the JWT.
     * @return The value of the specified claim as a String, or null if the
     *         Authorization header is not present or the claim cannot be found.
     */
    private static String fetchClaimFromJWT(MultiMap headers, String claim){
        // Check if the Authorization header is present
        if (headers.get(HttpHeaders.AUTHORIZATION) != null) {
            // Extract the access token by removing the "Bearer " prefix
            String accessToken = headers.get(HttpHeaders.AUTHORIZATION).replaceFirst("^Bearer ", "");
            // Decode the JWT token and extract the user ID claim
            return JWT.decode(accessToken).getClaim(claim).asString();
        }
        // Return an empty string if the Authorization header is not present
        return null;
    }

    /**
     * Retrieves the roles of the logged-in user from the Authorization header.
     *
     * @param headers the HTTP request headers containing the Authorization token
     * @return a list of roles associated with the user, or an empty list if no roles are found
     */
    public static List<String> getLoggedInUserRoles(MultiMap headers) {
        // Get the Authorization header, which should contain the Bearer token
        String authHeader = headers.get(HttpHeaders.AUTHORIZATION);

        // Check if the Authorization header is present and starts with "Bearer "
        if (authHeader != null) {
                // Extract the access token by removing the "Bearer " prefix
                String accessToken = authHeader.replaceFirst("^Bearer ", "");

                // Decode the JWT token to extract the claim that contains user roles
                String rolesClaim = JWT.decode(accessToken)
                        .getClaim(JWT_USER_ROLES_CLAIM.getPropertyName())
                        .asString();

                // If roles are found in the JWT claim, convert the JSON array to a List and return it
                if (rolesClaim != null) {
                    return new JsonArray(rolesClaim).getList();
                }
        }

        // If the Authorization header is missing or invalid, or no roles are found, return an empty list
        return Collections.emptyList();
    }



}