package org.apache.coyote.http11.request;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpRequestReader {

    private final InputStream inputStream;

    public HttpRequestReader(final InputStream inputStream) {
        this.inputStream = new BufferedInputStream(inputStream);
    }

    public Optional<HttpRequest> read() throws IOException {
        final String startLine = readLine();
        if (startLine == null) {
            return Optional.empty();
        }
        final RequestLine requestLine = RequestLine.from(startLine);
        final RequestHeaders headers = RequestHeaders.from(readHeaderLines());
        final String body = readBody(headers.getContentLength());

        return Optional.of(HttpRequest.of(requestLine, headers, body));
    }

    private String readLine() throws IOException {
        final ByteArrayOutputStream line = new ByteArrayOutputStream();
        int b = inputStream.read();
        if (b == -1) {
            return null;
        }
        while (b != -1 && b != '\n') {
            line.write(b);
            b = inputStream.read();
        }
        return stripCarriageReturn(line.toString(UTF_8));
    }

    private String stripCarriageReturn(final String line) {
        if (line.endsWith("\r")) {
            return line.substring(0, line.length() - 1);
        }
        return line;
    }

    private List<String> readHeaderLines() throws IOException {
        final List<String> headerLines = new ArrayList<>();
        String line = readLine();
        while (line != null && !line.isEmpty()) {
            headerLines.add(line);
            line = readLine();
        }
        return headerLines;
    }

    private String readBody(final int contentLength) throws IOException {
        final byte[] body = inputStream.readNBytes(contentLength);
        if (body.length < contentLength) {
            throw new HttpRequestParseException(
                    "Content-Length만큼 Body를 읽어오지 못했습니다: Content-Length=" + contentLength + ", Body=" + body.length);
        }
        return new String(body, UTF_8);
    }
}
