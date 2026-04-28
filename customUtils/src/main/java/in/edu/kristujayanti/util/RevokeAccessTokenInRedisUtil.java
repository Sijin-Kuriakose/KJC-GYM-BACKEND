package in.edu.kristujayanti.util;

import in.edu.kristujayanti.constants.AuthConstantKeys;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class for revoking access tokens associated with a specific user from a Redis store.
 */
public class RevokeAccessTokenInRedisUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(RevokeAccessTokenInRedisUtil.class);

    private final RedisAPI redisAPI;
    private final String redisHashKey;

    /**
     * Constructs a RevokeAccessTokenInRedisUtil instance with default Redis hash key.
     *
     * @param redisAPI Redis client instance used for interacting with the Redis server
     */
    public RevokeAccessTokenInRedisUtil(RedisAPI redisAPI){
        this(redisAPI, AuthConstantKeys.JWT_TOKENS_HASH_KEY);
    }

    /**
     * Constructs a RevokeAccessTokenInRedisUtil instance with a specified Redis hash key.
     *
     * @param redisAPI Redis client instance used for interacting with the Redis server
     * @param redisHashKey The Redis hash key where the tokens are stored (e.g., "jwtTokens", "accessTokens")
     */
    public RevokeAccessTokenInRedisUtil(RedisAPI redisAPI, String redisHashKey){
        this.redisAPI = redisAPI;
        this.redisHashKey = redisHashKey;
    }

    /**
     * Revokes all refresh tokens associated with a specific user by deleting the corresponding token entries from Redis.
     *
     * @param userId The user ID for whom all associated refresh tokens will be revoked
     * @return {@link Future<Boolean>} indicating the success (true) or failure (false) of the operation
     */
    public Future<Boolean> revokeUserTokens(String userId) {
        Promise<Boolean> promise = Promise.promise();

        try {
            // Fetch all token fields in the JWT token hash
            redisAPI.hkeys(this.redisHashKey)
                    .onSuccess(keys -> {
                        // Step 1: Filter keys matching the pattern for the userId (e.g., "userId:*")
                        List<Response> keysToDelete = keys.stream()
                                .filter(key -> key.toString().startsWith(userId + ":"))  // Match user's tokens
                                .toList();

                        if (!keysToDelete.isEmpty()) {
                            // Step 2: Iterate and delete each key individually
                            AtomicBoolean allSuccess = new AtomicBoolean(true);  // Track overall success
                            AtomicInteger count = new AtomicInteger(0);

                            for (Response key : keysToDelete) {
                                redisAPI.hdel(List.of(this.redisHashKey, key.toString()))
                                        .onSuccess(deletedCount -> {
                                            LOGGER.info("Revoked token for user: {} (Key: {})", userId, key);

                                            if (count.incrementAndGet() == keysToDelete.size() && allSuccess.get()) {
                                                // All tokens processed successfully
                                                promise.complete(true);
                                            }
                                        })
                                        .onFailure(err -> {
                                            allSuccess.set(false);
                                            LOGGER.error("Error revoking token for user: {} <{}>", userId, err.getMessage());
                                            promise.complete(false);  // Fail if any token fails to be deleted
                                        });
                            }
                        } else {
                            LOGGER.warn("No tokens found to revoke for user: {}", userId);
                            promise.complete(true);  // No tokens to revoke, considered successful
                        }
                    })
                    .onFailure(err -> {
                        LOGGER.error("Error fetching tokens for user: {} <{}>", userId, err.getMessage());
                        promise.fail(err);  // Fail if the token fetch fails
                    });

        } catch (Exception e) {
            LOGGER.error("Unexpected error while revoking all refresh tokens for user: {} <{}>", userId, e.getMessage());
            promise.fail(e);  // Fail if there's an unexpected error
        }

        return promise.future();
    }

    /**
     * Revokes a specific token associated with a specific user by deleting the corresponding token entry from Redis.
     *
     * @param userId      The user ID for whom the token is to be revoked.
     * @param accessToken The access token to be revoked.
     * @return {@link Future<Boolean>} indicating the success (true) or failure (false) of the operation.
     */
    public Future<Boolean> revokeUserToken(String userId, String accessToken) {
        Promise<Boolean> promise = Promise.promise();

        try {
            // Construct the exact key for the user's access token (e.g., "userId:accessToken")
            String tokenKey = userId + ":" + accessToken;

            // Delete the specific key from the Redis hash
            redisAPI.hdel(List.of(this.redisHashKey, tokenKey))
                    .onSuccess(deletedCount -> {
                        if (deletedCount.toInteger() > 0) {
                            LOGGER.info("Successfully revoked token for user: {} (Key: {})", userId, tokenKey);
                            promise.complete(true);
                        } else {
                            LOGGER.warn("No matching token found for user: {} with accessToken: {}", userId, accessToken);
                            promise.complete(false);  // No token found to delete
                        }
                    })
                    .onFailure(err -> {
                        LOGGER.error("Error revoking token for user: {} with accessToken: {} <{}>", userId, accessToken, err.getMessage());
                        promise.fail(err);  // Fail if the deletion fails
                    });

        } catch (Exception e) {
            LOGGER.error("Unexpected error while revoking token for user: {} with accessToken: {} <{}>", userId, accessToken, e.getMessage());
            promise.fail(e);  // Fail if there's an unexpected error
        }

        return promise.future();
    }

}