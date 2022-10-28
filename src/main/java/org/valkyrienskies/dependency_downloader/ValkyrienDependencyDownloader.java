package org.valkyrienskies.dependency_downloader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ValkyrienDependencyDownloader {
    public static final AtomicBoolean hasAlreadyRun = new AtomicBoolean(false);

    public static void main(String[] args) {
        System.out.println("Starting!!");
        start(Paths.get("mods"));
    }

    public static void start(Path modPath) {
        if (hasAlreadyRun.getAndSet(true)) return;

        List<ModDependency> requirements = DependencyAnalyzer.scanRequirements(modPath);

        DependencyPrompter prompter = new DependencyPrompter(modPath, requirements);
        prompter.promptToDownload();
    }
}
