package org.valkyrienskies.dependency_downloader.matchers;

@FunctionalInterface
public interface DependencyMatcherFactory {

    DependencyMatcherFactory FABRIC = FabricDependencyMatcher::new;
    DependencyMatcherFactory FORGE = ForgeDependencyMatcher::new;

    DependencyMatcher create(ModSpecification specification);

    default DependencyMatcher create(String modId, String versionRange) {
        return create(new ModSpecification(modId, versionRange));
    }

}
