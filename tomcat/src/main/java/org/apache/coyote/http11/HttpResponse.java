package org.apache.coyote.http11;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public record HttpResponse(StatusLine statusLine, HttpHeaders headers, byte[] body) {

    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    public static HttpResponse createForwardResponse(final StatusLine statusLine, final String resourceName, final HttpHeaders responseHeaders) throws IOException {
        final byte[] body = readResource(resourceName);

        final int contentLength = body.length;
        if (contentLength > 0) {
            final String contentType = URLConnection.guessContentTypeFromName(resourceName);
            responseHeaders.add("Content-Type", contentType + ";charset=utf-8 ");
        }
        responseHeaders.add("Content-Length", contentLength + "");

        return new HttpResponse(statusLine, responseHeaders, body);
    }

    public static HttpResponse createRedirectResponse(final String protocolVersion, final String redirectURL, final HttpHeaders responseHeaders) {
        final StatusLine statusLine = new StatusLine(protocolVersion, HttpStatusCode.FOUND);
        responseHeaders.add("Location", redirectURL);
        responseHeaders.add("Content-Length", "0");

        return new HttpResponse(statusLine, responseHeaders, new byte[0]);
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
