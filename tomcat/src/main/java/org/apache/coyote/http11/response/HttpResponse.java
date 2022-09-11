package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.apache.coyote.http11.common.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Headers headers;
    private StatusLine statusLine;
    private String body;

    public HttpResponse() {
        this.headers = new Headers();
    }

    public void ok(final String pathUri) {
        body = findResource(pathUri);
        headers.setContentType(ContentType.findByUri(pathUri));
        headers.setContentLength(body);
        statusLine = StatusLine.from(StatusCode.OK);
    }

    private String findResource(final String pathUri) {
        final URL url = getClass().getClassLoader().getResource("static" + pathUri);
        Objects.requireNonNull(url);
        try {
            final File file = new File(url.getPath());
            final Path path = file.toPath();
            return new String(Files.readAllBytes(path));
        } catch (final IOException e) {
            log.error("invalid resource", e);
            throw new IllegalArgumentException("파일을 찾을 수 없습니다.");
        }
    }

    public void redirect(final String redirectUri) {
        statusLine = StatusLine.from(StatusCode.FOUND);
        headers.setLocation(redirectUri);
    }

    public void setStatus(final StatusCode statusCode) {
        this.statusLine = StatusLine.from(statusCode);
    }

    public void setHeaders(final ContentType contentType) {
        headers.setContentType(contentType);
        headers.setContentLength(body);
    }

    public void setHeaders(final String location, final String cookie) {
        headers.setLocation(location);
        headers.setCookie(cookie);
    }

    public void setBody(final String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return String.join("\r\n", statusLine.toString(),
                headers.toString(),
                "",
                body);
    }
}
