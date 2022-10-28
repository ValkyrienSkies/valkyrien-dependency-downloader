package org.valkyrienskies.dependency_downloader;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ValkyrienDependencyDownloader {
    public static final AtomicBoolean hasAlreadyRun = new AtomicBoolean(false);

    public static void start(Path modPath) {
        start(modPath, x -> true);
    }

    public static void start(Path modPath, Predicate<ModDependency> shouldLoad) {
        if (hasAlreadyRun.getAndSet(true)) return;

        List<ModDependency> requirements = DependencyAnalyzer.scanRequirements(modPath)
            .stream().filter(shouldLoad).collect(Collectors.toList());

        DependencyPrompter prompter = new DependencyPrompter(modPath, requirements);
        prompter.promptToDownload();
    }
}
