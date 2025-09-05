package org.apache.coyote.http11.handle.handler.resource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.handle.handler.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class StaticResourceHandler implements HttpHandler {

    private static final Logger log = LoggerFactory.getLogger(StaticResourceHandler.class);

    public static final String STATIC_RESOURCE_PREFIX = "static";

    protected String readFile(final String uri) {
        final Path path = getStaticFilePath(uri);
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new IllegalStateException("파일을 읽는 도중 에러가 발생했습니다. " + path, e);
        }
    }

    private Path getStaticFilePath(final String uri) {
        final String staticResourceUri = STATIC_RESOURCE_PREFIX + uri;
        final URL resource = getClass().getClassLoader().getResource(staticResourceUri);
        if (resource == null) {
            throw new IllegalArgumentException("해당 리소스를 찾을 수 없습니다. " + staticResourceUri);
        }

        return new File(resource.getFile()).toPath();
    }
}
