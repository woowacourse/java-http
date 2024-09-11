package org.apache.catalina.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import org.apache.catalina.exception.CatalinaException;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceReader {

    private static final String BASE_PATH = "static";
    private static final String HTML_EXTENSION = ".html";
    private static final String END_OF_LINE = "";

    private static final Logger log = LoggerFactory.getLogger(ResourceReader.class);

    private ResourceReader() {

    }

    public static String probeContentType(String path) {
        Tika tika = new Tika();
        try {
            return tika.detect(getPath(path));
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new CatalinaException("Invalid path: " + path);
        }
    }

    public static String read(String path) {
        try {
            Path resourcePath = getPath(path);
            validateFileExists(resourcePath);
            List<String> fileLines = Files.readAllLines(resourcePath);
            return concatFileLines(fileLines);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new CatalinaException("Invalid path: " + path);
        }
    }

    private static Path getPath(String path) {
        URL resource = getResource(path);
        if (Objects.isNull(resource)) {
            throw new CatalinaException("Could not find resource: " + path);
        }

        try {
            return Path.of(resource.toURI());
        } catch (URISyntaxException e) {
            throw new CatalinaException("Cannot convert to URI: " + resource);
        }
    }

    public static URL getResource(String path) {
        String resourcePath = BASE_PATH + path;
        if (!path.contains(".")) {
            resourcePath = extendHtml(resourcePath);
        }
        return ResourceReader.class.getClassLoader().getResource(resourcePath);
    }

    private static String extendHtml(String path) {
        return path + HTML_EXTENSION;
    }

    private static void validateFileExists(Path path) throws FileNotFoundException {
        if (!Files.exists(path)) {
            throw new FileNotFoundException(path.toString());
        }
    }

    private static String concatFileLines(List<String> fileLines) {
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        for (String fileLine : fileLines) {
            joiner.add(fileLine);
        }
        joiner.add(END_OF_LINE);
        return joiner.toString();
    }
}
