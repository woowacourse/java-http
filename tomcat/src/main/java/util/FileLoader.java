package util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import org.apache.coyote.http11.exception.notfound.NotFoundUrlException;

public class FileLoader {

    private static final String STATIC_DIRECTORY = "static";

    private FileLoader() {
    }

    public static File loadFile(final String fileLocation) {
        final URL url = Thread.currentThread().getContextClassLoader().getResource(STATIC_DIRECTORY + fileLocation);
        if (Objects.isNull(url)) {
            throw new NotFoundUrlException();
        }
        return new File(url.getFile());
    }

    public static String readFile(final File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()));
    }
}
