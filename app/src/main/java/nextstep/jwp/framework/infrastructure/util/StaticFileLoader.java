package nextstep.jwp.framework.infrastructure.util;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticFileLoader {

    private static final Logger log = LoggerFactory.getLogger(StaticFileLoader.class);

    private StaticFileLoader() {
    }

    public static List<Path> loadStaticFilePaths(String staticDirectoryPath) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL resource = classLoader.getResource(staticDirectoryPath);
            File staticDirectoryRoot = new File(resource.getFile());
            return findStaticFilePaths(staticDirectoryRoot);
        } catch (RuntimeException runtimeException) {
            log.error("Static File Load Error");
            throw runtimeException;
        }
    }

    private static List<Path> findStaticFilePaths(File directory) {
        List<Path> filePaths = new ArrayList<>();
        if (!directory.exists()) {
            return filePaths;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            examineFileAttribute(file, filePaths);
        }
        return filePaths;
    }

    private static void examineFileAttribute(File file, List<Path> filePaths) {
        if (file.isDirectory()) {
            filePaths.addAll(findStaticFilePaths(file));
            return;
        }
        filePaths.add(file.toPath());
    }
}
