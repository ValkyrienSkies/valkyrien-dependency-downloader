package org.valkyrienskies.dependency_downloader;

import java.util.Objects;

public class ModDependency {
    private final DependencyMatcher matcher;
    private final String downloadUrl;
    private final boolean optional;

    private final String name;

    public ModDependency(DependencyMatcher matcher, String downloadUrl, boolean optional, String name) {
        this.matcher = matcher;
        this.downloadUrl = downloadUrl;
        this.optional = optional;
        this.name = name;
    }

    public DependencyMatcher getMatcher() {
        return matcher;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public boolean isOptional() {
        return optional;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModDependency that = (ModDependency) o;
        return optional == that.optional && Objects.equals(matcher, that.matcher) && Objects.equals(downloadUrl, that.downloadUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matcher, downloadUrl, optional);
    }

    public String getName() {
        return name;
    }
}
