package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpStatus.BAD_REQUEST;

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
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Cookie";

    private final HttpMethod httpMethod;
    private final String uri;
    private final Map<String, String> headers;
    private final HttpCookie httpCookie;
    private final String body;

    private HttpRequest(HttpMethod httpMethod, String uri, Map<String, String> headers, String body) {
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

        if (headers.containsKey(CONTENT_LENGTH)) {
            int contentLength = Integer.parseInt(headers.get(CONTENT_LENGTH));
            String body = getBody(bufferedReader, contentLength);
            return new HttpRequest(httpMethod, uri, headers, body);
        }
        return new HttpRequest(httpMethod, uri, headers, null);
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

    public String getBody() {
        if (body == null) {
            throw new HttpException(BAD_REQUEST, "요청 본문이 존재하지 않습니다");
        }
        return body;
    }

    public HttpMethod method() {
        return httpMethod;
    }

    public String uri() {
        return uri;
    }
}
