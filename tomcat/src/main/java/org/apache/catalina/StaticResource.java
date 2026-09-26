package org.apache.catalina;

import org.apache.coyote.MimeType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public record StaticResource(MimeType mimeType, byte[] content) {

    public static StaticResource from(Path path) throws IOException {
        MimeType mimeType = MimeType.fromFileName(path.getFileName().toString());
        return new StaticResource(mimeType, Files.readAllBytes(path));
    }
}
