package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.exception.NotFoundException;

public class StaticResource {

    private static final String RESOURCE_EXTENSION_SEPARATOR = ".";

    private final URL resource;

    public StaticResource(final String path) {
        this.resource = getClass().getClassLoader().getResource("static" + path);
        if (this.resource == null) {
            throw new NotFoundException();
        }
    }

    public String getExtension() {
        String path = resource.getPath();
        final int extensionIndex = path.lastIndexOf(RESOURCE_EXTENSION_SEPARATOR);
        return path.substring(extensionIndex + 1);
    }

    public byte[] readFile() throws IOException {
        return Files.readAllBytes(new File(this.resource.getFile()).toPath());
    }
}
