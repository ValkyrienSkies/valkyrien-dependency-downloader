package org.valkyrienskies.dependency_downloader.matchers;

import com.github.zafarkhaja.semver.Version;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.valkyrienskies.dependency_downloader.DependencyMatchResult;
import org.valkyrienskies.dependency_downloader.DependencyMatcher;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.Objects;

public class FabricDependencyMatcher implements DependencyMatcher {

    private final String modId;
    private final String versionRange;

    public FabricDependencyMatcher(String modId, String versionRange) {
        this.modId = modId;
        this.versionRange = versionRange;
    }

    @Override
    public DependencyMatchResult matches(FileSystem zip) {
        try {
            String fabricJson = new String(Files.readAllBytes(zip.getPath("fabric.mod.json")));
            JsonObject obj = new JsonParser().parse(fabricJson).getAsJsonObject();
            String id = obj.get("id").getAsString();
            String version = obj.get("version").getAsString();
            if (Objects.equals(modId, id)) {
                if (Version.valueOf(version).satisfies(versionRange)) {
                    return DependencyMatchResult.FULFILLED;
                } else {
                    return DependencyMatchResult.REPLACE;
                }
            }
        } catch (Exception e) {
            return DependencyMatchResult.PASS;
        }

        return DependencyMatchResult.PASS;
    }
}
