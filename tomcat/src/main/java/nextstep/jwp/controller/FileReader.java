package nextstep.jwp.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileReader {

    private static final String STATIC_FILE_PATH = "static";

    public static String read(final String filePath) throws IOException {
        final URL url = FileReader.class.getClassLoader().getResource(STATIC_FILE_PATH + filePath);
        final Path path = new File(url.getFile()).toPath();
        return Files.readString(path);
    }
}
