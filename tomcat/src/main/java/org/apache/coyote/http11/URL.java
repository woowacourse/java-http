package org.apache.coyote.http11;

import static org.apache.coyote.http11.request.Request.PATH_INDEX;
import static org.apache.coyote.http11.request.Request.QUERY_PARAM_DELIMITER_REGEX;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.exception.StaticFileNotFoundException;

public class URL {
    private static final String PATH_FROM_RESOURCE = "static";
    private static final String DEFAULT_URL = "/";
    private static final String NEWLINE = "\n";
    private static final String DEFAULT_MEDIA_TYPE = "text/html";
    private static final String DEFAULT_CONTENT = "Hello world!";


    private final String url;

    public URL(final String url) {
        this.url = url;
    }

    public static URL of(final String uri) {
        return new URL(uri.split(QUERY_PARAM_DELIMITER_REGEX)[PATH_INDEX]);
    }

    public String getMIMEType() throws IOException {
        if (this.isDefault() || !isForStaticFile()) {
            return DEFAULT_MEDIA_TYPE;
        }
        final Path path = new File(url).toPath();
        return Files.probeContentType(path);
    }

    public boolean isDefault() {
        return DEFAULT_URL.equals(url);
    }

    public String read() throws URISyntaxException, IOException {
        if (url == null || url.isBlank()) {
            return "";
        }
        if (isDefault()) {
            return DEFAULT_CONTENT;
        }
        final List<String> bodyLines = Files.readAllLines(getStaticFilePath());
        return String.join(NEWLINE, bodyLines) + NEWLINE;
    }

    private Path getStaticFilePath() throws URISyntaxException {
        String filePathString = PATH_FROM_RESOURCE + url;
        final URI uri = getUri(filePathString);
        return Paths.get(uri);
    }

    private URI getUri(final String filePathString) throws URISyntaxException {
        final java.net.URL fileUrl = Thread.currentThread().getContextClassLoader().getResource(filePathString);
        if (fileUrl == null) {
            throw new StaticFileNotFoundException(filePathString);
        }
        return fileUrl.toURI();
    }

    public boolean hasPath(final String path) {
        return this.url.equals(path);
    }

    public boolean isForStaticFile() {
        return url.contains(".");
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final URL url1 = (URL) o;
        return Objects.equals(url, url1.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}
