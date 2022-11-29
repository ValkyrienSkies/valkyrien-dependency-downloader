package org.valkyrienskies.dependency_downloader.matchers;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class ForgeLangDependencyMatcher implements DependencyMatcher {

    private final ModSpecification specification;

    public ForgeLangDependencyMatcher(ModSpecification specification) {
        this.specification = specification;
    }

    @Override
    public DependencyMatchResult matches(FileSystem zip) {
        Path manifestPath = zip.getPath("META-INF", "MANIFEST.MF");
        try {
            Manifest manifest = new Manifest(Files.newInputStream(manifestPath));
            Attributes attr = manifest.getMainAttributes();
            String modType = attr.getValue("FMLModType");
            if (!"LANGPROVIDER".equals(modType)) {
                return DependencyMatchResult.PASS;
            }

            String id = attr.getValue("Implementation-Title");
            String version = attr.getValue("Implementation-Version");

            return specification.getMatchResult(id, version);
        } catch (Exception e) {
            e.printStackTrace();
            return DependencyMatchResult.PASS;
        }
    }

    @Override
    public ModSpecification getSpecification() {
        return specification;
    }

    @Override
    public DependencyMatcherFactory getFactory() {
        return ForgeLangDependencyMatcher::new;
    }
}
