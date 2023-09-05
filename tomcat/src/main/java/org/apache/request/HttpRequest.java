package org.apache.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.common.Cookie;
import org.apache.common.HttpMethod;

public class HttpRequest {

    private final HttpStartLine httpStartLine;
    private final HttpHeaders httpHeaders;
    private final HttpBody httpBody;

    private HttpRequest(HttpStartLine httpStartLine, HttpHeaders httpHeaders, HttpBody body) {
        this.httpStartLine = httpStartLine;
        this.httpHeaders = httpHeaders;
        this.httpBody = body;
    }

    public static HttpRequest of(BufferedReader bufferedReader) throws IOException {
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
            if ("".equals(line)) {
                break;
            }
            lines.add(line);
        }

        return lines;
    }

    private static String extractBody(BufferedReader bufferedReader, HttpHeaders httpHeaders) throws IOException {
        if (!httpHeaders.containsHeader("Content-Length")) {
            return "";
        }
        int contentLength = Integer.parseInt(httpHeaders.getHeaderValue("Content-Length"));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public String getTarget() {
        return httpStartLine.getRequestTarget();
    }

    public String getBody() {
        return httpBody.getContent();
    }

    public HttpMethod getMethod() {
        return httpStartLine.getHttpMethod();
    }

    public Cookie getCookie() {
        if (!httpHeaders.containsHeader("Cookie")) {
            return Cookie.from("");
        }
        return Cookie.from(httpHeaders.getHeaderValue("Cookie"));
    }

    public boolean isPost() {
        return httpStartLine.getHttpMethod().equals(HttpMethod.POST);
    }

    public boolean isGet() {
        return httpStartLine.getHttpMethod().equals(HttpMethod.GET);
    }
}
