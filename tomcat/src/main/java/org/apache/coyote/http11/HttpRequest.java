package org.apache.coyote.http11;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HttpRequest {
    private static final String LINE_SEPARATOR = "\r\n";
    private static final String HEADER_NAME_VALUE_SEPARATOR = ":";
    private static final String COOKIE_HEADER = "Cookie";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    private static final String TRANSFER_ENCODING_HEADER = "Transfer-Encoding";
    private static final String CONTENT_LENGTH_PATTERN = "[0-9]+";
    private static final int MAX_HEADER_BYTES = 8 * 1024;
    private static final int MAX_BODY_BYTES = 1024 * 1024;

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final HttpCookie cookies;
    private final String requestBody;

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

        List<String> requestHeadLines = readRequestHead(bufferedInputStream);
        validateRequestLinePresence(requestHeadLines);

        requestLine = new RequestLine(requestHeadLines.getFirst());
        headers = parseHeaders(requestHeadLines.subList(1, requestHeadLines.size()));
        cookies = new HttpCookie(getHeader(COOKIE_HEADER));

        int contentLength = extractContentLength();
        requestBody = readRequestBody(bufferedInputStream, contentLength);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public String getHeader(String name) {
        return headers.getOrDefault(name, "");
    }

    public String getCookie(String name) {
        return cookies.getValue(name);
    }

    public String getRequestBody() {
        return requestBody;
    }

    private void validateRequestLinePresence(List<String> requestHeadLines) throws IOException {
        if (requestHeadLines.isEmpty()) {
            throw new IOException("요청 줄이 없습니다.");
        }
    }

    private List<String> readRequestHead(InputStream inputStream) throws IOException {
        List<String> requestHeadLines = new ArrayList<>();
        int headerBytesRead = 0;

        while (true) {
            String line = readHeaderLine(inputStream, headerBytesRead);
            headerBytesRead += line.getBytes(StandardCharsets.ISO_8859_1).length
                    + LINE_SEPARATOR.length();

            if (line.isEmpty()) {
                break;
            }

            requestHeadLines.add(line);
        }

        return requestHeadLines;
    }

    private String readHeaderLine(InputStream inputStream, int headerBytesRead) throws IOException {
        ByteArrayOutputStream lineBytes = new ByteArrayOutputStream();
        int previousByte = -1;

        while (true) {
            validateHeaderSize(headerBytesRead + lineBytes.size());

            int currentByte = readHeaderByte(inputStream);
            lineBytes.write(currentByte);

            if (previousByte == '\r' && currentByte == '\n') {
                break;
            }

            previousByte = currentByte;
        }

        String line = lineBytes.toString(StandardCharsets.ISO_8859_1);
        return line.substring(0, line.length() - LINE_SEPARATOR.length());
    }

    private int readHeaderByte(InputStream inputStream) throws IOException {
        int currentByte = inputStream.read();
        if (currentByte == -1) {
            throw new EOFException("요청 헤더를 읽는 중 입력이 종료되었습니다.");
        }

        return currentByte;
    }

    private void validateHeaderSize(int headerSize) throws IOException {
        if (headerSize >= MAX_HEADER_BYTES) {
            throw new IOException("요청 헤더의 최대 크기를 초과했습니다.");
        }
    }

    private Map<String, String> parseHeaders(List<String> headerLines) throws IOException {
        Map<String, String> parsedHeaders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        for (String headerLine : headerLines) {
            String[] headerParts = headerLine.split(HEADER_NAME_VALUE_SEPARATOR, 2);
            validateHeaderFormat(headerParts);

            String name = headerParts[0].trim();
            String value = headerParts[1].trim();

            validateContentLengthUniqueness(name, parsedHeaders);
            parsedHeaders.putIfAbsent(name, value);
        }

        return parsedHeaders;
    }

    private void validateHeaderFormat(String[] headerParts) throws IOException {
        if (headerParts.length != 2 || headerParts[0].isBlank()) {
            throw new IOException("올바르지 않은 요청 헤더입니다.");
        }
    }

    private void validateContentLengthUniqueness(String name, Map<String, String> parsedHeaders)
            throws IOException {
        if (name.equalsIgnoreCase(CONTENT_LENGTH_HEADER) && parsedHeaders.containsKey(name)) {
            throw new IOException("Content-Length가 중복되었습니다.");
        }
    }

    private int extractContentLength() throws IOException {
        validateTransferEncoding();

        String value = headers.get(CONTENT_LENGTH_HEADER);
        if (value == null) {
            return 0;
        }

        return parseContentLength(value);
    }

    private int parseContentLength(String value) throws IOException {
        validateContentLengthFormat(value);

        try {
            int contentLength = Integer.parseInt(value);
            validateBodySize(contentLength);
            return contentLength;
        } catch (NumberFormatException exception) {
            throw new IOException("Content-Length가 처리 범위를 초과했습니다.", exception);
        }
    }

    private void validateTransferEncoding() throws IOException {
        if (headers.containsKey(TRANSFER_ENCODING_HEADER)) {
            throw new IOException("지원하지 않는 본문 전송 방식입니다.");
        }
    }

    private void validateContentLengthFormat(String value) throws IOException {
        if (!value.matches(CONTENT_LENGTH_PATTERN)) {
            throw new IOException("Content-Length는 음이 아닌 정수여야 합니다.");
        }
    }

    private void validateBodySize(int contentLength) throws IOException {
        if (contentLength > MAX_BODY_BYTES) {
            throw new IOException("요청 본문의 최대 크기를 초과했습니다.");
        }
    }

    private String readRequestBody(InputStream inputStream, int contentLength)
            throws IOException {
        byte[] bodyBytes = inputStream.readNBytes(contentLength);

        if (bodyBytes.length != contentLength) {
            throw new EOFException("요청 본문을 모두 받기 전에 입력이 끝났습니다.");
        }

        return new String(bodyBytes, StandardCharsets.UTF_8);
    }
}
