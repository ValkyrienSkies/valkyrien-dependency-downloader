package org.valkyrienskies.dependency_downloader;

import javax.swing.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class DependencyDownloader {

    private final Path modPath;
    private final Path skipMarkerPath;
    private final Set<ModDependency> unsatisfiedDependencies;
    private final Set<DependencyToDownload> toDownload = new TreeSet<>();

    public DependencyDownloader(Path modPath, List<ModDependency> dependencies) {
        this.modPath = modPath;
        this.skipMarkerPath = modPath.resolve("valkryien_do_not_check_updates");
        this.unsatisfiedDependencies = new HashSet<>(dependencies);
    }


    public void promptToDownload() {
        if (Files.exists(skipMarkerPath)) return;

        checkAllJars();
        generateUnsatisfiedDependencies();
        if (!toDownload.isEmpty()) {
            createDownloadWindow();
        }
    }

    private DownloadWindow window;

    private void createDownloadWindow() {
        window = new DownloadWindow(toDownload);
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

        if (window.shouldDownload) {
            long startTime = System.nanoTime();
            downloadDependencies(window.getSelected());
            double time = (System.nanoTime() - startTime) / 1e9;
            JOptionPane.showMessageDialog(window, String.format("Download finished in %.1f seconds. You may have to manually restart the game.", time));
            System.exit(0);
        }

        if (window.doNotAskCheckbox.isSelected()) {
            try {
                Files.createFile(skipMarkerPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        window.dispose();
    }

    private void downloadDependencies(Collection<DependencyToDownload> toDownload) {
        int downloaded = 0;
        window.downloadProgress.setVisible(true);
        window.downloadButton.setVisible(false);
        window.downloadProgress.setMaximum(toDownload.size());
        for (DependencyToDownload dep : toDownload) {
            downloadDependency(dep);
            window.downloadProgress.setValue(++downloaded);
        }
    }

    private void downloadDependency(DependencyToDownload toDownload) {
        try {
            ModDependency dep = toDownload.getDependency();
            HttpURLConnection conn = (HttpURLConnection) new URL(dep.getDownloadUrl()).openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            ReadableByteChannel urlChannel = Channels.newChannel(conn.getInputStream());

            toDownload.getToReplace().ifPresent(p -> {
                try {
                    Path backupPath = modPath.resolve("vs_old_dependencies");
                    Files.createDirectories(backupPath);
                    Files.move(p, backupPath.resolve(p.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            Path targetPath = modPath.resolve(dep.getDownloadUrl().substring(dep.getDownloadUrl().lastIndexOf('/') + 1));
            try (FileChannel targetChannel = FileChannel.open(targetPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                targetChannel.transferFrom(urlChannel, 0, Long.MAX_VALUE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(window, "Failed to download " + toDownload.getDependency().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void generateUnsatisfiedDependencies() {
        for (ModDependency dependency : unsatisfiedDependencies) {
            toDownload.add(new DependencyToDownload(dependency, null));
        }
    }

    private void checkAllJars() {
        try (Stream<Path> jars = Files.list(modPath)) {
            jars.filter(jar -> jar.getFileName().toString().endsWith(".jar"))
                .forEach(this::checkJar);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkJar(Path jar) {
        try (FileSystem zipfs = FileSystems.newFileSystem(jar, null)) {
            unsatisfiedDependencies.removeIf(dep -> {
                DependencyMatchResult match = dep.getMatcher().matches(zipfs);
                checkMatchResult(jar, dep, match);
                return match != DependencyMatchResult.PASS;
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkMatchResult(Path jar, ModDependency dependency, DependencyMatchResult match) {
        if (match == DependencyMatchResult.REPLACE) {
            toDownload.add(new DependencyToDownload(dependency, jar));
        }
    }
}
