package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpRequest {

    private static final String EMPTY = "";
    private static final String BLANK = " ";
    private static final String SEPARATOR = "\r\n";
    private static final String COLON = ": ";
    private static final String AND = "&";
    private static final String EQUAL = "=";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Cookie";

    private final HttpMethod httpMethod;
    private final String uri;
    private final Map<String, String> headers;
    private final HttpCookie httpCookie;
    private final Map<String, String> body;

    private HttpRequest(HttpMethod httpMethod, String uri, Map<String, String> headers, Map<String, String> body) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.headers = headers;
        httpCookie = HttpCookie.from(headers.get(COOKIE));
        this.body = body;
    }

    public static HttpRequest from(BufferedReader bufferedReader) throws IOException {
        String firstLine = bufferedReader.readLine();
        String[] splitByBlank = firstLine.split(BLANK);
        String method = splitByBlank[0];
        HttpMethod httpMethod = HttpMethod.from(method);
        String uri = splitByBlank[1];
        String requestHeader = getHeader(bufferedReader);
        String[] splitBySeparator = requestHeader.split(SEPARATOR);
        Map<String, String> headers = new HashMap<>();
        for (String line : splitBySeparator) {
            String[] header = line.split(COLON, 2);
            String headerName = header[0];
            String headerValue = header[1];
            headers.put(headerName, headerValue);
        }

        Map<String, String> body = new HashMap<>();
        if (headers.containsKey(CONTENT_LENGTH)) {
            int contentLength = Integer.parseInt(headers.get(CONTENT_LENGTH));
            String requestBody = getBody(bufferedReader, contentLength);
            String[] splitByAnd = requestBody.split(AND);
            for (String pair : splitByAnd) {
                String[] splitByEqual = pair.split(EQUAL, 2);
                String key = splitByEqual[0];
                String value = splitByEqual[1];
                body.put(key, value);
            }
        }
        return new HttpRequest(httpMethod, uri, headers, body);
    }

    private static String getHeader(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        StringBuilder stringBuilder = new StringBuilder();
        while (!EMPTY.equals(line)) {
            stringBuilder.append(line);
            stringBuilder.append("\r\n");
            line = bufferedReader.readLine();
        }
        return stringBuilder.toString();
    }

    private static String getBody(BufferedReader bufferedReader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public Optional<String> getHeader(String headerName) {
        if (headers.containsKey(headerName)) {
            return Optional.of(headers.get(headerName));
        }
        return Optional.empty();
    }

    public Optional<String> getCookie(String cookieName) {
        return httpCookie.getCookie(cookieName);
    }

    public Optional<String> getBody(String key) {
        if (body.containsKey(key)) {
            return Optional.of(body.get(key));
        }
        return Optional.empty();
    }

    public HttpMethod method() {
        return httpMethod;
    }

    public String uri() {
        return uri;
    }
}
