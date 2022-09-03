package org.apache.coyote.http11.urihandler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

public abstract class DefaultUriHandler implements UriHandler {

    protected String getResponseBody(String uri) throws IOException {
        final URL resource = getClass().getClassLoader().getResource(uri);
        Objects.requireNonNull(resource);
        final String file = resource.getFile();

        return new String(Files.readAllBytes(new File(file).toPath()));
    }
}
