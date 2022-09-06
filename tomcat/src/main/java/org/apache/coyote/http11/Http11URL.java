package org.apache.coyote.http11;

import static org.apache.coyote.http11.Http11Request.PATH_INDEX;
import static org.apache.coyote.http11.Http11Request.QUERY_PARAM_DELIMITER_REGEX;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import nextstep.jwp.exception.StaticFileNotFoundException;

public class Http11URL {
    private static final String PATH_FROM_RESOURCE = "static";
    private static final String DEFAULT_URL = "/";
    private static final String NEWLINE = "\n";
    private static final String DEFAULT_MEDIA_TYPE = "text/html";
    private static final String DEFAULT_CONTENT = "Hello world!";


    private final String url;

    public Http11URL(final String url) {
        this.url = url;
    }

    public static Http11URL of(final String uri) {
        return new Http11URL(uri.split(QUERY_PARAM_DELIMITER_REGEX)[PATH_INDEX]);
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
        final URL fileUrl = Thread.currentThread().getContextClassLoader().getResource(filePathString);
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
}
