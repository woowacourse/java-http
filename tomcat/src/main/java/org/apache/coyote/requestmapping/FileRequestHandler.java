package org.apache.coyote.requestmapping;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

public class FileRequestHandler implements Handler {

    private final static String STATIC = "static" + File.separator;

    private String url;

    public FileRequestHandler(final String url) {
        this.url = url;
    }

    public Path getStaticFilePath(final String url) {
        return getPath(url);
    }

    private Path getPath(final String url) {
        return Paths.get(Objects.requireNonNull(
                getClass()
                .getClassLoader()
                .getResource(STATIC + url))
                .getPath());
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        if (!getPath(url).toFile().exists()) {
            return HttpResponse.notFound().build();
        }
        return HttpResponse.ok().body(getStaticFilePath(url)).build();
    }
}
