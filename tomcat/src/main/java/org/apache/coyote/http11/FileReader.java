package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileReader {

    private static final String PREFIX_STATIC_RESOURCES = "/static";
    private static final String DEFAULT_FILE_NAME = "/default.html";

    public static String readResourceFile() throws URISyntaxException, IOException {
        URL resource = FileReader.class.getResource(PREFIX_STATIC_RESOURCES + DEFAULT_FILE_NAME);
        return Files.readString(Paths.get(resource.toURI()));
    }

    public static String readResourceFile(String fileName) throws URISyntaxException, IOException {
        URL resource = FileReader.class.getResource(PREFIX_STATIC_RESOURCES + fileName);
        if (resource == null) {
            resource = FileReader.class.getResource(PREFIX_STATIC_RESOURCES + DEFAULT_FILE_NAME);
        }
        return Files.readString(Paths.get(resource.toURI()));
    }
}
