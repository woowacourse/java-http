package org.apache.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.common.Cookie;
import org.apache.common.HttpBody;
import org.apache.common.HttpMethod;

public class HttpRequest {

    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String EMPTY_STRING = "";
    private static final String COOKIE = "Cookie";

    private final HttpStartLine httpStartLine;
    private final HttpHeaders httpHeaders;
    private final HttpBody httpBody;

    private HttpRequest(HttpStartLine httpStartLine, HttpHeaders httpHeaders, HttpBody body) {
        this.httpStartLine = httpStartLine;
        this.httpHeaders = httpHeaders;
        this.httpBody = body;
    }

    public static HttpRequest from(BufferedReader bufferedReader) throws IOException {
        HttpStartLine httpStartLine = HttpStartLine.of(extractFirstLine(bufferedReader));
        HttpHeaders httpHeaders = HttpHeaders.of(extractHeaderLines(bufferedReader));
        HttpBody httpBody = new HttpBody(extractBody(bufferedReader, httpHeaders));
        return new HttpRequest(httpStartLine, httpHeaders, httpBody);
    }

    private static String extractFirstLine(BufferedReader bufferedReader) throws IOException {
        return bufferedReader.readLine();
    }

    private static List<String> extractHeaderLines(BufferedReader bufferedReader) throws IOException {
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (EMPTY_STRING.equals(line)) {
                break;
            }
            lines.add(line);
        }

        return lines;
    }

    private static String extractBody(BufferedReader bufferedReader, HttpHeaders httpHeaders) throws IOException {
        if (!httpHeaders.containsHeader(CONTENT_LENGTH)) {
            return EMPTY_STRING;
        }
        int contentLength = Integer.parseInt(httpHeaders.getHeaderValue(CONTENT_LENGTH));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public String getPath() {
        return httpStartLine.getPath();
    }

    public String getBody() {
        return httpBody.getContent();
    }

    public Cookie getCookie() {
        if (!httpHeaders.containsHeader(COOKIE)) {
            return Cookie.from(EMPTY_STRING);
        }
        return Cookie.from(httpHeaders.getHeaderValue(COOKIE));
    }

    public boolean isPost() {
        return httpStartLine.getMethod().equals(HttpMethod.POST);
    }

    public boolean isGet() {
        return httpStartLine.getMethod().equals(HttpMethod.GET);
    }
}
