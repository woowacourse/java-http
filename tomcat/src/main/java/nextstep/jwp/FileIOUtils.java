package nextstep.jwp;

import java.net.URISyntaxException;
import java.nio.file.Path;

public class FileIOUtils {

    public static Path getPath(String resourcePath) throws URISyntaxException {
        return Path.of(Thread.currentThread()
                .getContextClassLoader()
                .getResource(resourcePath).toURI());
    }
}
