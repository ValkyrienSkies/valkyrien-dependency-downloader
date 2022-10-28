package org.valkyrienskies.dependency_downloader;


import java.io.Serializable;
import java.nio.file.Path;
import java.util.Optional;

public class DependencyToDownload implements Comparable<DependencyToDownload>, Serializable {

    private final String downloadUrl;
    private final boolean optional;
    private final String name;
    private final String toReplace;

    public DependencyToDownload(ModDependency dependency, String toReplace) {
        this.downloadUrl = dependency.getDownloadUrl();
        this.optional = dependency.isOptional();
        this.name = dependency.getName();
        this.toReplace = toReplace;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public boolean isOptional() {
        return optional;
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return getDownloadUrl().substring(getDownloadUrl().lastIndexOf('/') + 1);
    }

    public Optional<String> getToReplace() {
        return Optional.ofNullable(toReplace);
    }


    @Override
    public int compareTo(DependencyToDownload that) {
        if (this.isOptional() && !that.isOptional()) {
            return -1;
        }

        if (that.isOptional() && !this.isOptional()) {
            return 1;
        }

        return this.getName().compareToIgnoreCase(that.getName());
    }
}
