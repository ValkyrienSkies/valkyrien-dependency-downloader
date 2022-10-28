package org.valkyrienskies.dependency_downloader.matchers;

import com.github.zafarkhaja.semver.Version;
import com.github.zafarkhaja.semver.expr.Expression;
import com.github.zafarkhaja.semver.expr.ExpressionParser;

public class ModSpecification {

    private final Expression versionRange;
    private final String versionRangeStr;
    private final String modId;

    public ModSpecification(String modId, String versionRange) {
        this.versionRangeStr = versionRange;
        this.versionRange = ExpressionParser.newInstance().parse(versionRange);
        this.modId = modId;
    }

    public boolean isCorrectMod(String modId) {
        return this.modId.equals(modId);
    }

    public String getModId() {
        return modId;
    }

    public String getVersionRange() {
        return this.versionRangeStr;
    }

    public boolean isCorrectVersion(String version) {
        return Version.valueOf(version).satisfies(versionRange);
    }
}
