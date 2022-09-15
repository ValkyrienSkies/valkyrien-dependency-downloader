import org.valkyrienskies.dependency_downloader.DependencyDownloader;

import java.nio.file.Paths;
import java.util.Arrays;

import static org.valkyrienskies.dependency_downloader.matchers.StandardMatchers.Forge16.*;

public class DependencyDownloaderManualTestForge {

    public static void main(String[] args) {
        new DependencyDownloader(Paths.get("./mods"),
            Arrays.asList(
                VALKYRIEN_SKIES,
                CLOTH_CONFIG,
                KOTLIN_FOR_FORGE,
                ARCHITECTURY_API
            ))
            .promptToDownload();
    }

}
