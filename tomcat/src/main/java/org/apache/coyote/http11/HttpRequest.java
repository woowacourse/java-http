package org.apache.coyote.http11;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.catalina.Session;

public class HttpRequest {
    private static final String LINE_SEPARATOR = "\r\n";
    private static final String HEADER_NAME_VALUE_SEPARATOR = ":";
    private static final String COOKIE_HEADER = "Cookie";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final HttpCookie cookies;
    private final String requestBody;
    private Map<String, String> formParameters;
    private Session session;

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

    public String getFormParameter(String name) {
        if (formParameters == null) {
            formParameters = parseFormData();
        }
        return formParameters.get(name);
    }

    public Session getSession() {
        return session;
    }

    void setSession(Session session) {
        this.session = session;
    }

    private Map<String, String> parseFormData() {
        Map<String, String> parameters = new HashMap<>();
        for (String field : requestBody.split("&")) {
            int separatorIndex = field.indexOf('=');
            if (separatorIndex < 0) {
                continue;
            }

            String name = URLDecoder.decode(field.substring(0, separatorIndex),
                    StandardCharsets.UTF_8);
            String value = URLDecoder.decode(field.substring(separatorIndex + 1),
                    StandardCharsets.UTF_8);
            parameters.putIfAbsent(name, value);
        }
        return parameters;
    }

    private void validateRequestLinePresence(List<String> requestHeadLines) throws IOException {
        if (requestHeadLines.isEmpty()) {
            throw new IOException("요청 줄이 없습니다.");
        }
    }

    private List<String> readRequestHead(InputStream inputStream) throws IOException {
        List<String> requestHeadLines = new ArrayList<>();

        while (true) {
            String line = readHeaderLine(inputStream);
            if (line.isEmpty()) {
                break;
            }

            requestHeadLines.add(line);
        }

        return requestHeadLines;
    }

    private String readHeaderLine(InputStream inputStream) throws IOException {
        ByteArrayOutputStream lineBytes = new ByteArrayOutputStream();
        int previousByte = -1;

        while (true) {
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

    private Map<String, String> parseHeaders(List<String> headerLines) throws IOException {
        Map<String, String> parsedHeaders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        for (String headerLine : headerLines) {
            String[] headerParts = headerLine.split(HEADER_NAME_VALUE_SEPARATOR, 2);
            validateHeaderFormat(headerParts);

            String name = headerParts[0].trim();
            String value = headerParts[1].trim();

            parsedHeaders.putIfAbsent(name, value);
        }

        return parsedHeaders;
    }

    private void validateHeaderFormat(String[] headerParts) throws IOException {
        if (headerParts.length != 2 || headerParts[0].isBlank()) {
            throw new IOException("올바르지 않은 요청 헤더입니다.");
        }
    }

    private int extractContentLength() throws IOException {
        String value = headers.get(CONTENT_LENGTH_HEADER);
        if (value == null) {
            return 0;
        }

        try {
            int contentLength = Integer.parseInt(value);
            if (contentLength < 0) {
                throw new IOException("Content-Length는 음이 아닌 정수여야 합니다.");
            }
            return contentLength;
        } catch (NumberFormatException exception) {
            throw new IOException("Content-Length는 음이 아닌 정수여야 합니다.", exception);
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
