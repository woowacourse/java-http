package org.apache.coyote.response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.support.HttpException;

public class View {

    public Path toResourcePath(String uri) {
        try {
            final var classLoader = getClass().getClassLoader();
            final var url = classLoader.getResource("static" + uri);
            final var file = new File(url.getFile());
            return file.toPath();
        } catch (NullPointerException e) {
            throw HttpException.ofNotFound(e);
        }
    }

    public String findContent(Path path) {
        try {
            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            throw HttpException.ofInternalServerError(e);
        }
    }

    public String findContentType(Path path) {
        try {
            return String.format("%s;charset=utf-8", Files.probeContentType(path));
        } catch (IOException e) {
            throw HttpException.ofInternalServerError(e);
        }
    }
}
