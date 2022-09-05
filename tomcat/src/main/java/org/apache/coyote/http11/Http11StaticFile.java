package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.exception.StaticFileNotFoundException;

public class Http11StaticFile {

    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
    private static final String PATH_FROM_RESOURCE = "static";
    private static final String NEWLINE = "\n";

    private final Http11URL http11URL;
    private final String content;

    private Http11StaticFile(final Http11URL http11URL, final String content) {
        this.http11URL = http11URL;
        this.content = content;
    }

    public static Http11StaticFile of(final Http11URL urlPath) throws IOException, URISyntaxException {
        final String body = parseContent(urlPath);
        return new Http11StaticFile(urlPath, body);
    }

    private static String parseContent(final Http11URL http11URL) throws IOException, URISyntaxException {
        if (http11URL.isDefault()) {
            return DEFAULT_RESPONSE_BODY;
        }
        final List<String> bodyLines = Files.readAllLines(getStaticFilePath(http11URL));
        return String.join(NEWLINE, bodyLines) + NEWLINE;
    }

    private static Path getStaticFilePath(final Http11URL http11URL) throws URISyntaxException {
        String filePathString = PATH_FROM_RESOURCE + http11URL.getPath();
        final URI uri = getUri(filePathString);
        return Paths.get(uri);
    }

    private static URI getUri(final String filePathString) throws URISyntaxException {
        final URL fileUrl = Thread.currentThread().getContextClassLoader().getResource(filePathString);
        if (Objects.isNull(fileUrl)) {
            throw new StaticFileNotFoundException(filePathString);
        }
        return fileUrl.toURI();
    }

    public String getContentType() throws IOException {
        return http11URL.getMediaType();
    }

    public String getContent() {
        return content;
    }

    public long getByteLength() {
        return content.getBytes().length;
    }
}
