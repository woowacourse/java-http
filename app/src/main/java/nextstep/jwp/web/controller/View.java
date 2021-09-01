package nextstep.jwp.web.controller;

import nextstep.jwp.web.exception.InputException;
import nextstep.jwp.web.network.response.ContentType;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class View {

    private static final String ROOT_DIRECTORY = "static";

    private final String path;
    private final ContentType contentType;

    public View(String path) {
        this.path = parsePath(path);
        this.contentType = parseContentType(path);
    }

    private String parsePath(String path) {
        if (path.contains(".")) {
            final int periodIndex = path.lastIndexOf(".");
            return path.substring(0, periodIndex);
        }
        return path;
    }

    private ContentType parseContentType(String path) {
        if (path.contains(".")) {
            final int periodIndex = path.lastIndexOf(".");
            final String extension = path.substring(periodIndex + 1);
            return ContentType.find(extension);
        }
        return ContentType.HTML;
    }

    public String render() {
        return readFile(path + "." + contentType.getExtension());
    }

    private String readFile(String fileName) {
        try {
            final URL url = getClass().getClassLoader().getResource(ROOT_DIRECTORY + fileName);
            final Path path = Paths.get(Objects.requireNonNull(url).getPath());
            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new InputException(ROOT_DIRECTORY + fileName);
        }
    }

    public ContentType getContentType() {
        return contentType;
    }
}
