package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        this.requestLine = new RequestLine(bufferedReader.readLine());
        this.headers = parseHeaders(bufferedReader);
        this.body = parseBody(bufferedReader);
    }

    private Map<String, String> parseHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        while (true) {
            String header = bufferedReader.readLine();
            if (header.isBlank()) {
                break;
            }
            String[] splitHeader = header.split(":");
            headers.put(splitHeader[0].trim(), splitHeader[1].trim());
        }
        return headers;
    }

    private String parseBody(BufferedReader bufferedReader) throws IOException {
        String rawContentLength = headers.get("Content-Length");
        if (rawContentLength == null) {
            return null;
        }
        int contentLength = Integer.parseInt(rawContentLength);
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);

        return new String(buffer);
    }

    public HttpCookie getCookie() {
        return new HttpCookie(headers.get("Cookie"));
    }

    public boolean isGetMethod() {
        return "GET".equals(requestLine.getMethod());
    }

    public boolean isPostMethod() {
        return "POST".equals(requestLine.getMethod());
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Map<String, String> getBodyQueryString() {
        Map<String, String> queryStringPairs = new HashMap<>();
        for (String pairs : body.split("&")) {
            String[] pair = pairs.split("=");
            if (pair.length == 2) {
                queryStringPairs.put(pair[0], pair[1]);
            }
        }

        return queryStringPairs;
    }
}
