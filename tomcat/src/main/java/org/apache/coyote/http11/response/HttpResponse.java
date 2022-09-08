package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import org.apache.coyote.http11.common.Headers;
import org.apache.coyote.http11.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private StatusLine statusLine;
    private Headers headers;
    private String body;

    public HttpResponse() {
        this.body = "";
    }

    public void ok(final HttpRequest httpRequest) {
        final String requestUri = httpRequest.getUri();
        final ContentType contentType = ContentType.findByUri(requestUri);

        this.statusLine = StatusLine.from(httpRequest.getProtocolVersion(), StatusCode.OK);
        this.body = createResponseBody(requestUri);
        this.headers = Headers.from(Map.entry("Content-Type", contentType.getValue() + ";charset=utf-8"),
                Map.entry("Content-Length", String.valueOf(body.getBytes().length)));

    }

    public String createResponseBody(final String pathUri) {
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

    @Override
    public String toString() {
        return String.join("\r\n", statusLine.toString(),
                headers.toString(),
                "",
                body);
    }

    public void setStatus(final HttpRequest request, final StatusCode statusCode) {
        this.statusLine = StatusLine.from(request.getProtocolVersion(), statusCode);
    }

    public void setHeaders(final ContentType contentType) {
        this.headers = Headers.from(Map.entry("Content-Type", contentType.getValue() + ";charset=utf-8"),
                Map.entry("Content-Length", String.valueOf(body.getBytes().length)));
    }

    public void setHeaders(final String location, final String jSessionId) {
        this.headers = Headers.from(Map.entry("Location", location),
                Map.entry("Set-Cookie", "JSESSIONID=" + jSessionId));
    }

    public void setBody(final String body) {
        this.body = body;
    }

    public void redirect(final HttpRequest request, final String redirectUri) {
        this.statusLine = StatusLine.from(request.getProtocolVersion(), StatusCode.FOUND);
        this.headers = Headers.from(Map.entry("Location", redirectUri));
    }
}
