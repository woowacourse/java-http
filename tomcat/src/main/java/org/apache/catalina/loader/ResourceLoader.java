package org.apache.catalina.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.HttpStatus;

public class ResourceLoader {

    private static final String DEFAULT_BASE_PATH = "static";

    public ResourceLoader() {

    }

    public URL getResource(final String path) throws FileNotFoundException {
        URL resource = getClass().getClassLoader().getResource(DEFAULT_BASE_PATH + path);
        if (resource == null) {
            throw new FileNotFoundException("Resource not found: " + path);
        }
        return resource;
    }

    public byte[] getResourceAsBytes(final String path) throws IOException {
        URL resource = getResource(path);
        return Files.readAllBytes(new File(resource.getFile()).toPath());
    }

    public String getDefaultErrorPage(final HttpStatus httpStatus) {
        return String.format("""
                    <html>
                        <head><title>Error</title></head>
                        <body><h1>%s</h1></body>
                    </html>
                """, httpStatus.getCode() + " " + httpStatus.getReasonPhrase());
    }
}
