package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private StatusLine statusLine;
    private final HttpHeaders headers;
    private byte[] body;

    private HttpResponse(final StatusLine statusLine, final HttpHeaders headers, final byte[] body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse createDefaultResponse(final String protocolVersion, final HttpHeaders headers) {
        return new HttpResponse(
                new StatusLine(protocolVersion, HttpStatusCode.OK),
                headers,
                new byte[0]
        );
    }

    public void sendForwardResponse(final HttpStatusCode statusCode, final String resourceName) throws IOException {
        this.statusLine = new StatusLine(this.statusLine.protocolVersion(), statusCode);

        this.body = readResource(resourceName);

        final int contentLength = body.length;
        if (contentLength > 0) {
            final String contentType = URLConnection.guessContentTypeFromName(resourceName);
            this.headers.add("Content-Type", contentType + ";charset=utf-8 ");
        }
        this.headers.add("Content-Length", contentLength + "");
    }

    public void sendRedirectResponse(final String redirectURL) {
        this.statusLine = new StatusLine(this.statusLine.protocolVersion(), HttpStatusCode.FOUND);
        this.headers.add("Location", redirectURL);
        this.headers.add("Content-Length", "0");
    }

    @Override
    public String toString() {
        return statusLine.protocolVersion() + " " +
                statusLine.httpStatusCode().getStatusCode() + " " +
                statusLine.httpStatusCode().getReasonPhrase() + " \r\n" +
                headers.toString() +
                "\r\n" +
                new String(body);
    }

    public byte[] getBytes() {
        final String response = this.toString();
        return response.getBytes();
    }

    private static byte[] readResource(final String resourceName) throws IOException {
        final String resourcePath = "static" + (resourceName.startsWith("/") ? "" : "/") + resourceName;
        try {
            final URI resourceURI = Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource(resourcePath)).toURI();
            final Path path = Path.of(resourceURI);
            return Files.readAllBytes(path);
        } catch (NullPointerException e) {
            log.error("{} 자료가 존재하지 않습니다.", resourcePath);
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
        return new byte[0];
    }
}
