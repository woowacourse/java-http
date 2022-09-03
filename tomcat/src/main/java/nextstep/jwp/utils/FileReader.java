package nextstep.jwp.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class FileReader {

    private static final String STATIC_FILE_PATH = "static";
    private static final String FILE_DELIMITER = ".";

    public static Optional<String> readByPath(String requestUri) throws IOException {
        if (!requestUri.contains(FILE_DELIMITER)) {
            requestUri += ".html";
        }
        final URL resource = FileReader.class.getClassLoader().getResource(STATIC_FILE_PATH + requestUri);
        if (resource == null) {
            return Optional.empty();
        }
        final Path path = new File(resource.getPath()).toPath();
        final byte[] bytes = Files.readAllBytes(path);
        return Optional.of(new String(bytes));
    }
}
