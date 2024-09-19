package org.apache.coyote.util;

import org.apache.coyote.http11.MimeType;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileReader {

    private static final String STATIC = "static";
    private static final String HTML = ".html";
    private static final String INDEX_HTML = "/index.html";

    public static byte[] read(String path) throws URISyntaxException, IOException {
        String resourcePath = determineResourcePath(path);
        Path filePath = Paths.get(FileReader.class.getClassLoader().getResource(resourcePath).toURI());

        return Files.readAllBytes(filePath);
    }

    private static String determineResourcePath(String path) {
        if (path.equals("/")) {
            return STATIC + INDEX_HTML;
        }

        if (MimeType.isValidFileExtension(path)) {
            return STATIC + path;
        }

        return STATIC + path + HTML;
    }
}
