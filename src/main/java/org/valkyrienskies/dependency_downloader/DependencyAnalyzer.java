package org.valkyrienskies.dependency_downloader;

import com.google.gson.*;
import org.valkyrienskies.dependency_downloader.matchers.DependencyMatchResult;
import org.valkyrienskies.dependency_downloader.matchers.FabricDependencyMatcher;
import org.valkyrienskies.dependency_downloader.matchers.ForgeDependencyMatcher;
import org.valkyrienskies.dependency_downloader.matchers.ModSpecification;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class DependencyAnalyzer {





    public static List<ModDependency> scanRequirements(Path modPath) {
        final List<ModDependency> requirements = new ArrayList<>();

        try (Stream<Path> jars = Files.list(modPath)) {
            jars.filter(jar -> jar.getFileName().toString().endsWith(".jar")).forEach(jar -> {
                try (FileSystem zipfs = FileSystems.newFileSystem(jar, (ClassLoader) null)) {
                    Path manifest = zipfs.getPath("valkyrien_dependency_manifest.json");
                    if (Files.exists(manifest)) {
                        try {
                            requirements.addAll(parseManifest(new String(Files.readAllBytes(manifest))));
                        } catch (Exception e) {
                            System.out.println("VS: Failed to parse dependency manifest for " + jar);
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return requirements;
    }

    public static List<ModDependency> parseManifest(String manifestJson) {
        List<ModDependency> dependencies = new ArrayList<>();
        JsonArray deps = new JsonParser().parse(manifestJson).getAsJsonArray();
        for (JsonElement e : deps) {
            try {
                JsonObject dep = e.getAsJsonObject();
                String loader = dep.get("loader").getAsString();
                String downloadUrl = dep.get("downloadUrl").getAsString();
                String name = dep.get("name").getAsString();
                String versionRange = dep.get("versionRange").getAsString();
                String modId = dep.get("modId").getAsString();
                boolean optional = dep.has("optional") && dep.get("optional").getAsBoolean();

                ModSpecification spec = new ModSpecification(modId, versionRange);
                if ("fabric".equals(loader)) {
                    dependencies.add(new ModDependency(new FabricDependencyMatcher(spec), downloadUrl, optional, name));
                } else if ("forge".equals(loader)) {
                    dependencies.add(new ModDependency(new ForgeDependencyMatcher(spec), downloadUrl, optional, name));
                } else {
                    System.out.println("VS: Unrecognized manifest loader: " + loader);
                }
            } catch (Exception ex) {
                System.out.println("VS: Malformed dependency manifest entry " + e);
                ex.printStackTrace();
            }
        }
        return dependencies;
    }

    public static Set<DependencyToDownload> checkDependencies(Path modPath, Collection<ModDependency> dependencies) {
        Set<ModDependency> unsatisfiedDependencies = new HashSet<>(dependencies);
        Set<DependencyToDownload> toDownload = new HashSet<>();

        try (Stream<Path> jars = Files.list(modPath)) {
            jars.filter(jar -> jar.getFileName().toString().endsWith(".jar"))
                .forEach(jar -> {
                    try (FileSystem zipfs = FileSystems.newFileSystem(jar, (ClassLoader) null)) {
                        unsatisfiedDependencies.removeIf(dep -> {
                            DependencyMatchResult match = dep.getMatcher().matches(zipfs);
                            if (match == DependencyMatchResult.REPLACE) {
                                toDownload.add(new DependencyToDownload(dep, jar.toAbsolutePath().toString()));
                            }
                            return match != DependencyMatchResult.PASS;
                        });

                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (ModDependency dependency : unsatisfiedDependencies) {
            toDownload.add(new DependencyToDownload(dependency, null));
        }

        return toDownload;
    }

}
