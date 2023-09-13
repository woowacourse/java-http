package org.apache.coyote.http11.response;

import nextstep.jwp.exception.ResourceNotFoundException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileReader {
    private static final String STATIC = "static";

    private FileReader() {
    }

    public static String read(String url) {
        url = resolve(url);
        try {
            String path = FileReader.class.getClassLoader().getResource(url).getPath();
            return new String(Files.readAllBytes(new File(path).toPath()));
        } catch (NullPointerException | IOException e) {
            throw new ResourceNotFoundException(e);
        }
    }

    private static String resolve(String url) {
        if (url.equals("/")) {
            return url;
        }
        if (url.contains(".")) {
            return addStatic(url);
        }
        return addStatic(url + ".html");
    }

    private static String addStatic(String url) {
        return STATIC + url;
    }
}
