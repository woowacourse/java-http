package org.apache.coyote.http11;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class StaticResourceLoader {


    private File getResourceFile(String fileName) throws FileNotFoundException {
        URL resource = getClass()
                .getClassLoader()
                .getResource(fileName);

        if (resource == null) {
            throw new FileNotFoundException(fileName);
        }

        try {
            return Path.of(resource.toURI()).toFile();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public String load(String path) throws IOException {
        File resourceFile = getResourceFile("static" + path);

        return Files.readString(
                resourceFile.toPath(),
                StandardCharsets.UTF_8
        );
    }
}
