package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final String body;
    private final Map<String, String> parameters;
    private final HttpCookie cookie;

    public HttpRequest(RequestLine requestLine, Map<String, String> headers, String body) {
        this.requestLine = requestLine;
        this.headers = Map.copyOf(headers);
        this.body = body;
        this.parameters = readParameters(requestLine, body);
        this.cookie = new HttpCookie(headers.get("Cookie"));
    }

    public static HttpRequest from(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        RequestLine requestLine = RequestLine.of(reader.readLine());
        if (requestLine == null) {
            return null;
        }

        Map<String, String> headers = readHeaders(reader);
        String body = readBody(reader, requestLine, headers);
        return new HttpRequest(requestLine, headers, body);
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getBody() {
        return body;
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

    public String getCookie(String name) {
        return cookie.get(name);
    }

    private static Map<String, String> readHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        while (true) {
            String headerLine = reader.readLine();
            if (headerLine == null || headerLine.isEmpty()) {
                return headers;
            }
            String[] parts = headerLine.split(":", 2);
            headers.put(parts[0].strip(), parts[1].strip());
        }
    }

    private static String readBody(BufferedReader reader, RequestLine requestLine,
                                   Map<String, String> headers) throws IOException {
        String contentLengthHeader = headers.get("Content-Length");
        if (!"POST".equals(requestLine.getMethod()) || contentLengthHeader == null) {
            return "";
        }

        int contentLength = Integer.parseInt(contentLengthHeader);
        char[] buffer = new char[contentLength];
        int totalRead = 0;
        while (totalRead < contentLength) {
            int readLength = reader.read(buffer, totalRead, contentLength - totalRead);
            if (readLength == -1) {
                throw new EOFException("본문 읽기 실패");
            }
            totalRead += readLength;
        }
        return new String(buffer);
    }

    private static Map<String, String> readParameters(RequestLine requestLine, String body) {
        if ("POST".equals(requestLine.getMethod())) {
            return parseParameters(body);
        }
        return parseParameters(requestLine.getQueryString());
    }

    private static Map<String, String> parseParameters(String parameters) {
        Map<String, String> parametersByName = new HashMap<>();
        for (String parameter : parameters.split("&")) {
            String[] nameAndValue = parameter.split("=");
            if (nameAndValue.length != 2) {
                return Map.of();
            }
            String name = URLDecoder.decode(nameAndValue[0], StandardCharsets.UTF_8);
            String value = URLDecoder.decode(nameAndValue[1], StandardCharsets.UTF_8);
            parametersByName.put(name, value);
        }
        return parametersByName;
    }
}
