package org.apache.coyote.controller.resource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.error.ErrorCode;
import org.apache.coyote.error.HttpException;

public class StaticResourceReader {

    private static final StaticResourceReader instance = new StaticResourceReader();

    private StaticResourceReader() {
    }

    public static StaticResourceReader getInstance() {
        return instance;
    }

    public String getStaticResponseBody(final String fileUrl) throws IOException {
        try {
            final URI uri = getClass().getClassLoader()
                    .getResource(fileUrl)
                    .toURI();
            final Path htmlPath = Path.of(uri);
            final byte[] read = Files.readAllBytes(htmlPath);
            return new String(read, StandardCharsets.UTF_8);
        } catch (URISyntaxException e) {
            throw new HttpException(ErrorCode.NOT_EXISTS_STATIC_RESOURCE);
        }
    }
}
