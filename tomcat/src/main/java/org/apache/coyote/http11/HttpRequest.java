package org.apache.coyote.http11;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpRequest {

    private static final int END_OF_STREAM = -1;
    private static final int LINE_FEED = '\n';
    private static final int CARRIAGE_RETURN = '\r';

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final HttpCookie cookies;
    private final String body;

    public HttpRequest(final InputStream inputStream) throws IOException {
        final BufferedInputStream input = toBufferedInputStream(inputStream);

        this.requestLine = readRequestLine(input);
        this.headers = readHeaders(input);
        this.body = readBody(input, parseContentLength());
        this.cookies = new HttpCookie(headers.getHeader("Cookie"));
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public String getHttpVersion() {
        return requestLine.getHttpVersion();
    }

    public String getHeader(final String name) {
        return headers.getHeader(name);
    }

    public String getCookie(final String name) {
        return cookies.getValue(name);
    }

    public String getBody() {
        return body;
    }

    private BufferedInputStream toBufferedInputStream(final InputStream inputStream) {
        if (inputStream instanceof BufferedInputStream bufferedInput) {
            return bufferedInput;
        }
        return new BufferedInputStream(inputStream);
    }

    private RequestLine readRequestLine(final InputStream input) throws IOException {
        final String rawRequestLine = readLine(input);
        if (rawRequestLine == null) {
            throw new EOFException("요청 라인을 읽기 전에 연결이 종료됐습니다.");
        }
        return new RequestLine(rawRequestLine);
    }

    private HttpHeaders readHeaders(final InputStream input) throws IOException {
        final List<String> headerLines = new ArrayList<>();
        String line;
        while ((line = readLine(input)) != null && !line.isEmpty()) {
            headerLines.add(line);
        }
        if (line == null) {
            throw new EOFException("요청 헤더를 모두 읽기 전에 연결이 종료됐습니다.");
        }
        return new HttpHeaders(headerLines);
    }

    private int parseContentLength() throws IOException {
        final String contentLength = getHeader("Content-Length");
        if (contentLength == null) {
            return 0;
        }

        final int length;
        try {
            length = Integer.parseInt(contentLength);
        } catch (NumberFormatException e) {
            throw new IOException("잘못된 Content-Length입니다: " + contentLength, e);
        }
        if (length < 0) {
            throw new IOException("잘못된 Content-Length입니다: " + contentLength);
        }
        return length;
    }

    private String readBody(final InputStream input, final int contentLength) throws IOException {
        final byte[] body = input.readNBytes(contentLength);
        if (body.length < contentLength) {
            throw new EOFException("요청 본문을 모두 읽기 전에 연결이 종료됐습니다. expected="
                    + contentLength + ", actual=" + body.length);
        }
        return new String(body, StandardCharsets.UTF_8);
    }

    private String readLine(final InputStream input) throws IOException {
        final ByteArrayOutputStream lineBytes = new ByteArrayOutputStream();
        int currentByte;
        while ((currentByte = input.read()) != END_OF_STREAM && currentByte != LINE_FEED) {
            lineBytes.write(currentByte);
        }

        if (currentByte == END_OF_STREAM && lineBytes.size() == 0) {
            return null;
        }
        return lineBytesToStringWithoutCarriageReturn(lineBytes.toByteArray());
    }

    private String lineBytesToStringWithoutCarriageReturn(final byte[] lineBytes) {
        int lineLength = lineBytes.length;
        if (lineLength > 0 && lineBytes[lineLength - 1] == CARRIAGE_RETURN) {
            lineLength--;
        }
        return new String(lineBytes, 0, lineLength, StandardCharsets.ISO_8859_1);
    }
}
