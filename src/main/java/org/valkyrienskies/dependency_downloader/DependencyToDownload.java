package org.valkyrienskies.dependency_downloader;


import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;

public class DependencyToDownload implements Comparable<DependencyToDownload> {

    private final ModDependency dependency;
    private final Path toReplace;

    public DependencyToDownload(ModDependency dependency, Path toReplace) {
        this.dependency = dependency;
        this.toReplace = toReplace;
    }

    public Optional<Path> getToReplace() {
        return Optional.ofNullable(toReplace);
    }

    public ModDependency getDependency() {
        return dependency;
    }

    @Override
    public int compareTo(DependencyToDownload that) {
        if (this.dependency.isOptional() && !that.dependency.isOptional()) {
            return 1;
        }

        if (that.dependency.isOptional() && !this.dependency.isOptional()) {
            return -1;
        }

        return this.dependency.getName().compareToIgnoreCase(that.dependency.getName());
    }
}
