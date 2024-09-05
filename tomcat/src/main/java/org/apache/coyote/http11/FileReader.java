package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class FileReader {

    private final ResourceHandler resourceHandler;

    public FileReader(ResourceHandler resourceHandler) {
        this.resourceHandler = resourceHandler;
    }

    public String read() throws IOException {
        String fileName = resourceHandler.handle();
        URL resource = getClass().getClassLoader().getResource(fileName);

        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
