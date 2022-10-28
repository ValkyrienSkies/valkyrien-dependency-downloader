package org.valkyrienskies.dependency_downloader.matchers;

public enum DependencyMatchResult {

    /**
     * The provided JAR file fulfills this dependency
     */
    FULFILLED,

    /**
     * The provided JAR file is an old version of this dependency and needs to be replaced
     */
    REPLACE,

    /**
     * The provided JAR file is irrelevant
     */
    PASS

}
