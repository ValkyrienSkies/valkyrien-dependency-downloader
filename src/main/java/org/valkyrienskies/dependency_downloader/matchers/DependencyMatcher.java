package org.valkyrienskies.dependency_downloader.matchers;

import java.nio.file.FileSystem;

public interface DependencyMatcher {

    DependencyMatchResult matches(FileSystem zip);

    ModSpecification getSpecification();

    DependencyMatcherFactory getFactory();

}
