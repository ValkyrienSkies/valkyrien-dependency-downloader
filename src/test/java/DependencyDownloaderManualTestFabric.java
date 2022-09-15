import org.valkyrienskies.dependency_downloader.DependencyDownloader;

import java.nio.file.Paths;
import java.util.Arrays;

import static org.valkyrienskies.dependency_downloader.matchers.StandardMatchers.Fabric16.*;

public class DependencyDownloaderManualTestFabric {

    public static void main(String[] args) {
        new DependencyDownloader(Paths.get("./mods"),
            Arrays.asList(
                VALKYRIEN_SKIES,
                CLOTH_CONFIG,
                FABRIC_API,
                FABRIC_KOTLIN,
                ARCHITECTURY_API,
                MOD_MENU
            ))
            .promptToDownload();
    }

}
