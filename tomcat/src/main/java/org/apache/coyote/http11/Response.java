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
import java.util.Map;
import java.util.Map.Entry;

public class Response {

    private static final Map<Integer, String> STATUS = Map.ofEntries(
            Map.entry(200, "OK"),
            Map.entry(302, "Found")
    );

    private final OutputStream outputStream;
    private final Map<String, String> headers = new HashMap<>();

    public Response(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void addCookies(final Cookies cookies) {
        headers.put("Set-Cookie", cookies.toCookieList());
    }

    public void sendResource(final String resource) throws IOException {
        final URL resourceUrl = getResourceUrl(resource);
        final Path resourcePath = Paths.get(resourceUrl.getFile());

        byte[] messageBody = Files.readAllBytes(resourcePath);
        headers.put("Content-Type", Files.probeContentType(resourcePath));
        headers.put("Content-Length", String.valueOf(messageBody.length));

        outputStream.write(buildResponseLine(200).getBytes(StandardCharsets.UTF_8));
        outputStream.write(buildHeaders().getBytes(StandardCharsets.UTF_8));
        outputStream.write(messageBody);
        outputStream.flush();
    }

    public void sendRedirection(final String location) throws IOException {
        headers.put("Location", String.format("http://localhost:8080%s", location));
        headers.put("Content-Length", "0");

        outputStream.write(buildResponseLine(302).getBytes(StandardCharsets.UTF_8));
        outputStream.write(buildHeaders().getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private URL getResourceUrl(final String resource) throws FileNotFoundException {
        final URL resourceUrl = getClass().getClassLoader().getResource("static" + resource);
        if (resourceUrl == null) {
            throw new FileNotFoundException("요청한 리소스를 찾을 수 없습니다.");
        }
        return resourceUrl;
    }

    private String buildResponseLine(final int statusCode) {
        return String.format("HTTP/1.1 %d %s \r\n", statusCode, STATUS.get(statusCode));
    }

    private String buildHeaders() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final Entry<String, String> header : headers.entrySet()) {
            final String headerLine = String.format("%s: %s ", header.getKey(), header.getValue());
            stringBuilder.append(headerLine).append("\r\n");
        }
        stringBuilder.append("\r\n");
        return stringBuilder.toString();
    }
}
