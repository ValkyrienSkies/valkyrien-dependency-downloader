package org.valkyrienskies.dependency_downloader;

import org.valkyrienskies.dependency_downloader.gui.DownloadWindow;
import org.valkyrienskies.dependency_downloader.util.Utils;

import javax.annotation.Nullable;
import javax.swing.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class DependencyPrompter {

    private final Path modPath;
    private final Path modJarFile;
    private final Path skipMarkerPath;
    private Set<DependencyToDownload> toDownload;

    private final List<ModDependency> dependencies;

    public DependencyPrompter(Path modPath, Path modJarFile, List<ModDependency> dependencies) {
        this.modPath = modPath;
        this.skipMarkerPath = modPath.resolve("valkryien_do_not_check_updates");
        this.dependencies = dependencies;
        this.modJarFile = modJarFile;
    }


    public void promptToDownload() {
        if (Files.exists(skipMarkerPath)) return;

        toDownload = DependencyAnalyzer.checkDependencies(modPath, dependencies);

        if (!toDownload.isEmpty()) {
            createDownloadWindow();
        }
    }

    private void createDownloadWindow() {
        DownloadWindow window = new DownloadWindow(toDownload);
        window.setVisible(true);
        synchronized (window.lock) {
            while (!window.shouldContinue) {
                try {
                    window.lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (window.doNotAskCheckbox.isSelected()) {
            try {
                Files.createFile(skipMarkerPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        window.dispose();

        if (window.shouldDownload) {
            try {
                DependencyDownloader.DownloadData data = new DependencyDownloader.DownloadData(
                    modPath.toAbsolutePath().toString(),
                    window.getSelected(),
                    Utils.guessRestartCommand().orElse(null),
                    System.getenv()
                );

                Path jarFile = Optional.ofNullable(modJarFile).orElseGet(this::getJarFile);

                Process process = new ProcessBuilder(
                    Utils.guessJavaCommand(),
                    "-cp",
                    jarFile.toAbsolutePath().toString(),
                    "org.valkyrienskies.dependency_downloader.DependencyDownloader"
                ).directory(null).start();

                ObjectOutputStream oos = new ObjectOutputStream(process.getOutputStream());
                oos.writeObject(data);
                oos.close();

                System.exit(0);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to start the downloader, " +
                    "download the dependencies manually, exiting..");
                System.exit(-1);
            }
        }
    }

    private Path getJarFile() {
        try {
            return Paths.get(DependencyDownloader.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        } catch (Exception ignore) {}

        try (Stream<Path> jars = Files.list(modPath)) {
            return jars.filter(jar -> jar.getFileName().toString().endsWith(".jar"))
                .filter(jar -> {
                    try (FileSystem zipfs = FileSystems.newFileSystem(jar, (ClassLoader) null)) {
                        zipfs.getPath("org", "valkyrienskies", "dependency_downloader", "DependencyDownloader.class");
                        return true;
                    } catch (IOException e) {
                        return false;
                    }
                })
                .findAny()
                .get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
