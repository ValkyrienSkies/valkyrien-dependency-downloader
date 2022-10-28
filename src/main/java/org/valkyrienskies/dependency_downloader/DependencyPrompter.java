package org.valkyrienskies.dependency_downloader;

import org.valkyrienskies.dependency_downloader.gui.DownloadWindow;
import org.valkyrienskies.dependency_downloader.util.Utils;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

public class DependencyPrompter {

    private final Path modPath;
    private final Path skipMarkerPath;
    private Set<DependencyToDownload> toDownload;

    private final List<ModDependency> dependencies;

    public DependencyPrompter(Path modPath, List<ModDependency> dependencies) {
        this.modPath = modPath;
        this.skipMarkerPath = modPath.resolve("valkryien_do_not_check_updates");
        this.dependencies = dependencies;
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
                    Utils.guessRestartCommand().orElse(null)
                );

                Path jarFile = Paths.get(DependencyDownloader.class.getProtectionDomain().getCodeSource().getLocation().toURI());
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


}
