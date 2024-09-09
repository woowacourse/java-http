package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

public class StaticResourceLoader {

    private static final String STATIC_RESOURCE_DIRECTORY = "static";

    public String load(String path) {
        URL resource = getClass().getClassLoader().getResource(STATIC_RESOURCE_DIRECTORY + path);
        if (resource == null) {
            return "";
        }
        try {
            File file = new File(resource.toURI());
            return Files.readString(file.toPath());
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
