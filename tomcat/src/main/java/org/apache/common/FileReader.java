package org.apache.common;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileReader {

    private static final String DEFAULT_RESOURCE_LOCATION = "static";

    private FileReader() {
    }

    public static String read(String path) throws IOException {
        URI uri = convertPathToUri(path);

        return Files.readString(Paths.get(uri));
    }


    private static URI convertPathToUri(String path) {
        URL url = FileReader.class.getClassLoader()
                .getResource(DEFAULT_RESOURCE_LOCATION + path);
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
