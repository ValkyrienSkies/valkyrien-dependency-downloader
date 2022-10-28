package org.valkyrienskies.dependency_downloader;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ValkyrienDependencyDownloader {
    public static final AtomicBoolean hasAlreadyRun = new AtomicBoolean(false);

    public static void start(Path modPath, boolean isDedicatedServer) {
        start(modPath, x -> true, null, isDedicatedServer);
    }

    public static void start(Path modPath, Path modJarFile, boolean isDedicatedServer) {
        start(modPath, x -> true, modJarFile, isDedicatedServer);
    }

    public static void start(Path modPath, Predicate<ModDependency> shouldLoad, boolean isDedicatedServer) {
        start(modPath, shouldLoad, null, isDedicatedServer);
    }

    public static void start(Path modPath, Predicate<ModDependency> shouldLoad, Path modJarFile, boolean isDedicatedServer) {
        if (hasAlreadyRun.getAndSet(true)) return;

        List<ModDependency> requirements = DependencyAnalyzer.scanRequirements(modPath)
            .stream().filter(shouldLoad).collect(Collectors.toList());

        if (isDedicatedServer) {
            String reqsStr = requirements.stream()
                .map(d -> d.getName() + ", version " + d.getMatcher().getSpecification().getVersionRange())
                .collect(Collectors.joining("\n"));

            System.out.println("You are missing the following dependencies!\n" + reqsStr);
        }

        DependencyPrompter prompter = new DependencyPrompter(modPath, modJarFile, requirements);
        prompter.promptToDownload();
    }
}
