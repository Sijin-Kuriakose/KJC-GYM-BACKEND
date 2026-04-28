package in.edu.kristujayanti.router;

import io.vertx.ext.web.Router;

/**
 * RouterBuilderService interface defines the contract for building routers.
 * Classes implementing this interface should provide implementations for creating a router and getting the path for the router.
 */
public interface RouterBuilderService {

    /**
     * Creates and configures a Vert.x Router instance.
     *
     * @return the configured Router instance.
     */
    Router createRouter();

    /**
     * Gets the path associated with the router.
     * This path is used to attach the router to a parent router.
     *
     * @return the path as a String.
     */
    String getPath();
}
