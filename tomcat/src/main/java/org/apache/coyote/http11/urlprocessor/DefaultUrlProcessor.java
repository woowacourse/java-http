package org.apache.coyote.http11.urlprocessor;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

public abstract class DefaultUrlProcessor implements UrlProcessor {

    protected String getResponseBody(String url) throws IOException {
        final URL resource = getClass().getClassLoader().getResource(url);
        Objects.requireNonNull(resource);
        final String file = resource.getFile();

        return new String(Files.readAllBytes(new File(file).toPath()));
    }
}
