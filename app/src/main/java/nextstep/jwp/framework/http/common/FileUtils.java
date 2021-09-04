package nextstep.jwp.framework.http.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {

    public static String probeContentType(final Path path) throws IOException {
        return Files.probeContentType(path);
    }
}
