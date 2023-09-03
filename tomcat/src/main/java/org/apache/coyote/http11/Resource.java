package org.apache.coyote.http11;

import org.apache.coyote.http11.exception.ResourceNotFoundException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

public class Resource {

    private Resource() {
    }

    public static String readResource(final String resourcePath) throws IOException {
        final URL resource = Thread.currentThread().getContextClassLoader().getResource(resourcePath);
        try {
            Objects.requireNonNull(resource);
        } catch (NullPointerException e) {
            throw new ResourceNotFoundException(resourcePath);
        }
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
