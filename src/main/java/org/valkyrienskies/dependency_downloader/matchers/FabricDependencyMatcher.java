package org.valkyrienskies.dependency_downloader.matchers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.valkyrienskies.dependency_downloader.DependencyMatchResult;
import org.valkyrienskies.dependency_downloader.DependencyMatcher;

import java.nio.file.FileSystem;
import java.nio.file.Files;

public class FabricDependencyMatcher implements DependencyMatcher {

    private final ModSpecification specification;

    public FabricDependencyMatcher(ModSpecification specification) {
        this.specification = specification;
    }

    @Override
    public DependencyMatchResult matches(FileSystem zip) {
        try {
            String fabricJson = new String(Files.readAllBytes(zip.getPath("fabric.mod.json")));
            JsonObject obj = new JsonParser().parse(fabricJson).getAsJsonObject();
            String id = obj.get("id").getAsString();
            String version = obj.get("version").getAsString();
            if (specification.isCorrectMod(id)) {
                if (specification.isCorrectVersion(version)) {
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
