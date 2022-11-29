package org.valkyrienskies.dependency_downloader;

import com.github.zafarkhaja.semver.Version;
import com.github.zafarkhaja.semver.expr.CompositeExpression;
import com.github.zafarkhaja.semver.expr.Expression;
import com.google.gson.JsonObject;
import org.valkyrienskies.dependency_downloader.matchers.DependencyMatcher;
import org.valkyrienskies.dependency_downloader.matchers.FabricDependencyMatcher;
import org.valkyrienskies.dependency_downloader.matchers.ForgeDependencyMatcher;
import org.valkyrienskies.dependency_downloader.matchers.ModSpecification;

import java.util.Objects;

public class ModDependency {
    private final DependencyMatcher matcher;
    private final String downloadUrl;
    private final boolean optional;

    private final String name;

    private final Version versionFor;


    public ModDependency(DependencyMatcher matcher, String downloadUrl, boolean optional, String name) {
        this.matcher = matcher;
        this.downloadUrl = downloadUrl;
        this.optional = optional;
        this.name = name;
        this.versionFor = Version.forIntegers(0);
    }

    public ModDependency(DependencyMatcher matcher, String downloadUrl, boolean optional, String name, Version versionFor) {
        this.matcher = matcher;
        this.downloadUrl = downloadUrl;
        this.optional = optional;
        this.name = name;
        this.versionFor = versionFor;
    }

    public ModDependency merge(ModDependency other) {
        ModSpecification otherSpec = other.getMatcher().getSpecification();
        ModSpecification thisSpec = this.getMatcher().getSpecification();

        String modId = thisSpec.getModId();

        if (!otherSpec.getModId().equals(modId)) {
            throw new IllegalArgumentException("Tried to merge mods " + modId + " and " + otherSpec.getModId() + " with different mod IDs");
        }

        Expression combinedVersionRange = new CompositeExpression(otherSpec.getVersionExpression())
            .and(thisSpec.getVersionExpression());

        String combinedVersionRangeStr = thisSpec.getVersionRange().equals(otherSpec.getVersionRange())
            ? thisSpec.getVersionRange()
            : "(" + otherSpec.getVersionRange() + ") & (" + thisSpec.getVersionRange() + ")";

        String newDownloadUrl = getDownloadUrl();
        Version versionFor = this.versionFor;
        if (other.versionFor.greaterThan(this.versionFor)) {
            newDownloadUrl = other.getDownloadUrl();
            versionFor = other.versionFor;
        }

        ModSpecification newSpec = new ModSpecification(combinedVersionRange, combinedVersionRangeStr, modId);
        DependencyMatcher newMatcher = getMatcher().getFactory().create(newSpec);

        return new ModDependency(newMatcher, newDownloadUrl, other.optional && this.optional, getName(), versionFor);
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

    public String asJson() {
        String loader = "forge";
        if (getMatcher() instanceof FabricDependencyMatcher) loader = "fabric";

        JsonObject json = new JsonObject();
        json.addProperty("loader", loader);
        json.addProperty("name", getName());
        json.addProperty("modId", getMatcher().getSpecification().getModId());
        json.addProperty("versionRange", getMatcher().getSpecification().getVersionRange());
        json.addProperty("optional", isOptional());
        json.addProperty("downloadUrl", getDownloadUrl());
        return json.toString();
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
