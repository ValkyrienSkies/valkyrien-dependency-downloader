package org.valkyrienskies.dependency_downloader.matchers;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.FileConfig;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.List;

public class ForgeDependencyMatcher implements DependencyMatcher {
    private final ModSpecification specification;

    public ForgeDependencyMatcher(ModSpecification specification) {
        this.specification = specification;
    }

    @Override
    public DependencyMatchResult matches(FileSystem zip) {
        Path modsToml = zip.getPath("META-INF", "mods.toml");
        try (FileConfig config = FileConfig.of(modsToml)) {
            config.load();

            List<UnmodifiableConfig> mods = config.get("mods");
            for (UnmodifiableConfig modConfig : mods) {
                String modId = modConfig.get("modId");
                String version = modConfig.get("version");

                if (specification.isCorrectMod(modId)) {
                    if (specification.isCorrectVersion(version)) {
                        return DependencyMatchResult.FULFILLED;
                    } else if (mods.size() == 1) { // replace only if this mod to be updated is the only mod in the jar file
                        return DependencyMatchResult.REPLACE;
                    }
                }
            }
        } catch (Exception e) {
            return DependencyMatchResult.PASS;
        }

        return DependencyMatchResult.PASS;
    }

    @Override
    public ModSpecification getSpecification() {
        return specification;
    }

    @Override
    public DependencyMatcherFactory getFactory() {
        return ForgeDependencyMatcher::new;
    }
}
