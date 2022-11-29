package org.valkyrienskies.dependency_downloader.matchers;

import com.github.zafarkhaja.semver.Version;
import com.github.zafarkhaja.semver.expr.CompositeExpression;
import com.github.zafarkhaja.semver.expr.Expression;
import com.github.zafarkhaja.semver.expr.ExpressionParser;

import static com.github.zafarkhaja.semver.expr.CompositeExpression.Helper.gte;

public class ModSpecification {

    private final Expression versionRange;
    private final String versionRangeStr;
    private final String modId;

    public ModSpecification(Expression versionRange, String versionRangeStr, String modId) {
        this.versionRange = versionRange;
        this.versionRangeStr = versionRangeStr;
        this.modId = modId;
    }

    public ModSpecification(String modId) {
        this.modId = modId;
        this.versionRange = gte(Version.forIntegers(0, 0, 0));
        this.versionRangeStr = "*";
    }

    public ModSpecification(String modId, String versionRange) {
        this.versionRangeStr = versionRange;
        this.versionRange = ExpressionParser.newInstance().parse(versionRange);
        this.modId = modId;
    }

    public ModSpecification(String modId, String minVersion, String maxVersion) {
        this.modId = modId;

        String versionRangeStr = "";
        CompositeExpression expr = gte(Version.forIntegers(0, 0, 0));

        if (minVersion != null) {
            versionRangeStr += ">=" + minVersion;
            expr = expr.and(CompositeExpression.Helper.gte(minVersion));
        }

        if (maxVersion != null) {
            if (!versionRangeStr.isEmpty()) versionRangeStr += " & ";
            versionRangeStr += "<=" + maxVersion;

            expr = expr.and(CompositeExpression.Helper.lte(maxVersion));
        }

        this.versionRangeStr = versionRangeStr;
        this.versionRange = expr;
    }

    public DependencyMatchResult getMatchResult(String id, String version) {
        if (isCorrectMod(id)) {
            if (isCorrectVersion(version)) {
                return DependencyMatchResult.FULFILLED;
            } else {
                return DependencyMatchResult.REPLACE;
            }
        }
        return DependencyMatchResult.PASS;
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

    public Expression getVersionExpression() {
        return this.versionRange;
    }

    public boolean isCorrectVersion(String version) {
        return Version.valueOf(version).satisfies(versionRange);
    }
}
