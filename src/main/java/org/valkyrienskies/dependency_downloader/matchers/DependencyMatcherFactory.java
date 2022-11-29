package org.valkyrienskies.dependency_downloader.matchers;

@FunctionalInterface
public interface DependencyMatcherFactory {

    DependencyMatcherFactory FABRIC = FabricDependencyMatcher::new;
    DependencyMatcherFactory FORGE = ForgeDependencyMatcher::new;

    DependencyMatcherFactory FORGE_LANG = ForgeLangDependencyMatcher::new;

    static DependencyMatcherFactory getMatcher(String name) {
        switch (name) {
            case "fabric":
                return FABRIC;
            case "forge":
                return FORGE;
            case "forge_lang":
                return FORGE_LANG;
            default:
                throw new IllegalArgumentException("VS dependencies unrecognized loader in manifest: " + name);
        }
    }

    DependencyMatcher create(ModSpecification specification);

    default DependencyMatcher create(String modId, String versionRange) {
        return create(new ModSpecification(modId, versionRange));
    }

}
