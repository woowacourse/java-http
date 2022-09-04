package org.apache.coyote.http11.util;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.apache.coyote.http11.exception.ReadFileException;
import org.apache.coyote.http11.exception.StaticFileNotFoundException;

public class StaticFileUtil {

    private static final String STATIC_DIRECTORY = "static";

    private StaticFileUtil() {
    }

    public static String readFile(final String path) {
        ClassLoader classLoader = StaticFileUtil.class.getClassLoader();
        URL resource = classLoader.getResource(STATIC_DIRECTORY + path);
        if (Objects.isNull(resource)) {
            throw new StaticFileNotFoundException(path);
        }

        try {
            return Files.readString(Path.of(resource.getPath()));
        } catch (IOException e) {
            throw new ReadFileException();
        }
    }
}
