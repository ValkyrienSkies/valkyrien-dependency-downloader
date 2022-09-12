package org.valkyrienskies.dependency_downloader.matchers;

import org.valkyrienskies.dependency_downloader.ModDependency;

public class StandardMatchers {

    public static final String MAVEN_URL = "https://maven.valkyrienskies.org";


    public static ModDependency fabricCurseDependency(String name, String modId, String versionRange, String projectId, String fileId, boolean optional) {
        return new ModDependency(
            new FabricDependencyMatcher(modId, versionRange),
            curseDownloadUrl(modId, projectId, fileId),
            optional,
            name
        );
    }

    public static String curseDownloadUrl(String modId, String projectId, String fileId) {
        String archiveName = modId + "-" + projectId;
        return MAVEN_URL + "/curse/maven/" + archiveName + "/" + fileId + "/" + archiveName + "-" + fileId + ".jar";
    }

    public static class Fabric16 {
        public static final ModDependency VALKYRIEN_SKIES = new ModDependency(
            new FabricDependencyMatcher("valkyrienskies", ">=2.0.0"),
            MAVEN_URL + "/org/valkyrienskies/valkyrienskies-116-fabric/2.0.0+a97d61c6a4/valkyrienskies-116-fabric-2.0.0+a97d61c6a4.jar",
            false,
            "Valkyrien Skies"
        );

        public static final ModDependency FABRIC_KOTLIN = new ModDependency(
            new FabricDependencyMatcher("fabric-language-kotlin", ">=1.8.3"),
            MAVEN_URL + "/net/fabricmc/fabric-language-kotlin/1.8.3+kotlin.1.7.10/fabric-language-kotlin-1.8.3+kotlin.1.7.10.jar",
            false,
            "Fabric Language Kotlin"
        );

        public static final ModDependency CLOTH_CONFIG = new ModDependency(
            new FabricDependencyMatcher("cloth-config2", ">=4.14.64"),
            MAVEN_URL + "/me/shedaniel/cloth/cloth-config-fabric/4.14.64/cloth-config-fabric-4.14.64.jar",
            false,
            "Cloth Config"
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
