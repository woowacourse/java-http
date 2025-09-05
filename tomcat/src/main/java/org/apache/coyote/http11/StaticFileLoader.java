package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class StaticFileLoader {
    private static final String PREFIX = "static";

    private File findFileWithUri(final String fileUri) {
        URL systemResource = ClassLoader.getSystemResource(PREFIX + fileUri);
        if (systemResource == null) {
            throw new IllegalArgumentException();
        }

        return new File(systemResource.getFile());
    }

    public byte[] readAllFileWithUri(final String fileUri) throws IOException {
        return Files.readAllBytes(findFileWithUri(fileUri).toPath());
    }
}
