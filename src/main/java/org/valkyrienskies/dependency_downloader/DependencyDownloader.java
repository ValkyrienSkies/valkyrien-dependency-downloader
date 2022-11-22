package org.valkyrienskies.dependency_downloader;

import org.valkyrienskies.dependency_downloader.gui.DownloadingProgress;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.*;
import java.util.Collection;
import java.util.Map;

public class DependencyDownloader {
    private final DownloadData data;
    private DownloadingProgress window;

    public DependencyDownloader(DownloadData data) {
        this.data = data;
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        DownloadData data = (DownloadData) new ObjectInputStream(System.in).readObject();
        Thread.sleep(500); // give the game some time to shut down or something idk
        new DependencyDownloader(data).start();
    }

    private void start() {
        window = new DownloadingProgress();
        window.setVisible(true);

        int downloaded = 0;
        long startTime = System.nanoTime();

        window.totalProgress.setMaximum(data.toDownload.size());
        for (DependencyToDownload dep : data.toDownload) {
            window.currentlyDownloading.setText("Currently downloading: " + dep.getName());
            downloadDependency(dep);
            window.totalProgress.setValue(++downloaded);
        }

        double time = (System.nanoTime() - startTime) / 1e9;

        JOptionPane.showMessageDialog(window, String.format("Download finished in %.1f seconds. Please manually restart the game.", time));
        System.exit(0);
    }

    private void downloadDependency(DependencyToDownload toDownload) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(toDownload.getDownloadUrl()).openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            ReadableByteChannel urlChannel = Channels.newChannel(conn.getInputStream());

            Path modPath = Paths.get(data.modPath);

            toDownload.getToReplace().ifPresent(pathStr -> {
                Path p = Paths.get(pathStr);
                try {
                    Path backupPath = modPath.resolve("vs_old_dependencies");
                    Files.createDirectories(backupPath);
                    Files.move(p, backupPath.resolve(p.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                    throw new UncheckedIOException(e);
                }
            });

            Path targetPath = modPath.resolve(toDownload.getFileName());
            try (FileChannel targetChannel = FileChannel.open(targetPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                targetChannel.transferFrom(urlChannel, 0, Long.MAX_VALUE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(window, "Failed to download " + toDownload.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static class DownloadData implements Serializable {

        final String modPath;
        final Collection<DependencyToDownload> toDownload;

        public DownloadData(String modPath, Collection<DependencyToDownload> toDownload) {
            this.modPath = modPath;
            this.toDownload = toDownload;
        }
    }

}
