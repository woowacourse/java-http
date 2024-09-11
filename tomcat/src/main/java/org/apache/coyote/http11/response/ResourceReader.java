package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;

public class ResourceReader {

    private static final String BASE_PATH = "static/";

    private ResourceReader() {
    }

    public static String read(String resource) throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        Optional<URL> url = Optional.ofNullable(classLoader.getResource(BASE_PATH + resource));

        if (url.isPresent()) {
            File file = new File(url.get().getFile());
            return new String(Files.readAllBytes(file.toPath()));
        }

        throw new NullPointerException("파일이 존재하지 않습니다.");
    }
}
