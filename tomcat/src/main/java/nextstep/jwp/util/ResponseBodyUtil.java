package nextstep.jwp.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResponseBodyUtil {

    public static String alter(final Path path) throws IOException {
        final byte[] content = Files.readAllBytes(path);
        return new String(content);
    }
}
