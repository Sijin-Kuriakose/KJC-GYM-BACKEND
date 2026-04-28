package in.edu.kristujayanti.router;

import in.edu.kristujayanti.constants.SubRouterKeys;
import org.reflections.Reflections;

import java.util.Set;

/**
 * SubRouterBuilders is a utility class responsible for discovering and providing all sub-router builder classes.
 * It uses the Reflections library to scan the specified package for classes extending SubRouterBuilderBase.
 */
public final class SubRouterBuilders {
    private SubRouterBuilders(){}

    // A set of all classes that extend SubRouterBuilderBase
    private static final Set<Class<? extends SubRouterBuilderBase>> subRouterClasses;

    // Static block to initialize the subRouterClasses set
    static {
        // The package name where the sub-router classes are located
        String packageName = SubRouterKeys.SUB_ROUTER_PACKAGE_NAME;

        // Using Reflections to scan the package for subclasses of SubRouterBuilderBase
        Reflections reflections = new Reflections(packageName);

        // Getting all subtypes of SubRouterBuilderBase in the specified package
        subRouterClasses = reflections.getSubTypesOf(SubRouterBuilderBase.class);
    }

    /**
     * Returns the set of sub-router builder classes discovered in the specified package.
     *
     * @return Set of classes extending SubRouterBuilderBase.
     */
    public static Set<Class<? extends SubRouterBuilderBase>> getSubRouterClasses() {
        return subRouterClasses;
    }
}
