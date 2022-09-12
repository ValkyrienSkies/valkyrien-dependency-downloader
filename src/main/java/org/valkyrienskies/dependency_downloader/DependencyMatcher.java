package org.valkyrienskies.dependency_downloader;

import java.nio.file.FileSystem;

public interface DependencyMatcher {

    DependencyMatchResult matches(FileSystem zip);

}
