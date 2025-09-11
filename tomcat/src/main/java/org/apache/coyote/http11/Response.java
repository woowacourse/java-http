package org.apache.coyote.http11;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Map;
import java.util.Map.Entry;

public class Response {

    private static final Map<Integer, String> STATUS = Map.ofEntries(
            Map.entry(200, "OK"),
            Map.entry(302, "Found")
    );
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";
    private static final String HOST = "http://localhost:8080";
    private static final String CRLF = "\r\n";

    private final OutputStream outputStream;
    private final Map<String, String> headers;

    public Response(final OutputStream outputStream) {
        this.outputStream = outputStream;
        this.headers = new HashMap<>();
    }

    public void addCookies(final Cookies cookies) {
        headers.put(SET_COOKIE, cookies.formatToCookieList());
    }

    public void sendResource(final String resource) throws IOException {
        try {
            final URL resourceUrl = getResourceUrl(resource);
            final Path resourcePath = Paths.get(resourceUrl.getFile());

            byte[] messageBody = Files.readAllBytes(resourcePath);
            headers.put(CONTENT_TYPE, Files.probeContentType(resourcePath));
            headers.put(CONTENT_LENGTH, String.valueOf(messageBody.length));

            outputStream.write(buildResponseLine(200).getBytes(StandardCharsets.UTF_8));
            outputStream.write(buildHeaders().getBytes(StandardCharsets.UTF_8));
            outputStream.write(messageBody);
            outputStream.flush();
        } catch (
                final NullPointerException |
                      IOException |
                      OutOfMemoryError |
                      SecurityException e) {
            throw new IOException("Resource를 전송할 수 없습니다.", e);
        }
    }

    public void sendRedirection(final String location) throws IOException {
        try {
            headers.put(LOCATION, String.format("%s%s", HOST, location));
            headers.put(CONTENT_LENGTH, "0");

            outputStream.write(buildResponseLine(302).getBytes(StandardCharsets.UTF_8));
            outputStream.write(buildHeaders().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (final IOException | IllegalFormatException | NullPointerException e) {
            throw new IOException("Redirection을 전송할 수 없습니다.", e);
        }
    }

    private URL getResourceUrl(final String resource) throws FileNotFoundException {
        final URL resourceUrl = getClass().getClassLoader().getResource("static" + resource);
        if (resourceUrl == null) {
            throw new FileNotFoundException("요청한 리소스를 찾을 수 없습니다.");
        }
        return resourceUrl;
    }

    private String buildResponseLine(final int statusCode) {
        return String.format("HTTP/1.1 %d %s " + CRLF, statusCode, STATUS.get(statusCode));
    }

    private String buildHeaders() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final Entry<String, String> header : headers.entrySet()) {
            final String headerLine = String.format("%s: %s ", header.getKey(), header.getValue());
            stringBuilder.append(headerLine).append(CRLF);
        }
        stringBuilder.append(CRLF);
        return stringBuilder.toString();
    }
}
