package org.valkyrienskies.dependency_downloader.matchers;

import org.valkyrienskies.dependency_downloader.ModDependency;

import javax.annotation.Nullable;

public class StandardMatchers {

    public static final String MAVEN_URL = "https://maven.valkyrienskies.org";

    public static ModDependency fabricValkyrienSkiesDependency(String versionRange, String latestVersion) {
        return fabricMavenDependency(
            "Valkyrien Skies",
            "valkyrienskies",
            versionRange,
            "org.valkyrienskies",
            "valkyrienskies-116-fabric",
            latestVersion,
            null,
            false
        );
    }

    public static ModDependency fabricMavenDependency(
        String name, String modId, String versionRange,
        String group, String archive, String version, String classifier, boolean optional
    ) {
        return new ModDependency(
            new FabricDependencyMatcher(modId, versionRange),
            mavenDownloadUrl(group, archive, version, classifier),
            optional,
            name
        );
    }

    public static ModDependency fabricCurseDependency(String name, String modId, String versionRange, String projectId, String fileId, boolean optional) {
        return new ModDependency(
            new FabricDependencyMatcher(modId, versionRange),
            curseDownloadUrl(modId, projectId, fileId),
            optional,
            name
        );
    }

    public static String mavenDownloadUrl(String group, String archive, String version, @Nullable String classifier) {
        classifier = classifier == null ? "" : "-" + classifier;
        return MAVEN_URL + "/" + group.replace('.', '/') + "/" + archive + "/" + version + "/" +
            archive + "-" + version + classifier + ".jar";
    }

    public static String curseDownloadUrl(String modId, String projectId, String fileId) {
        String archiveName = modId + "-" + projectId;
        return MAVEN_URL + "/curse/maven/" + archiveName + "/" + fileId + "/" + archiveName + "-" + fileId + ".jar";
    }

    public static class Fabric16 {
        public static final ModDependency VALKYRIEN_SKIES = fabricValkyrienSkiesDependency(">=2.0.0", "2.0.0+a97d61c6a4");

        public static final ModDependency FABRIC_KOTLIN = fabricMavenDependency(
            "Fabric Language Kotlin",
            "fabric-language-kotlin",
            ">=1.8.3",
            "net.fabricmc",
            "fabric-language-kotlin",
            "1.8.3+kotlin.1.7.10",
            null,
            false);

        public static final ModDependency CLOTH_CONFIG = fabricMavenDependency(
            "Cloth Config API",
            "cloth-config2",
            ">=4.14.64",
            "me.shedaniel.cloth",
            "cloth-config-fabric",
            "4.14.64",
            null,
            false
        );

        public static final ModDependency FABRIC_API = fabricCurseDependency(
            "Fabric API",
            "fabric",
            ">=0.42.0",
            "306612",
            "3516413",
            false
        );

        public static final ModDependency ARCHITECTURY_API = fabricCurseDependency(
            "Architectury",
            "architectury",
            ">=1.32.66",
            "419699",
            "3857642",
            false
        );

        public static final ModDependency MOD_MENU = fabricCurseDependency(
            "Mod Menu",
            "modmenu",
            ">=1.16.22",
            "308702",
            "3850092",
            true
        );
    }


}
