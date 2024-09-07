package org.apache.catalina;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> header;
    private final HttpCookie httpCookie;
    private final String body;  // TODO: 왜 안쓰이는지 보기

    public HttpRequest(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        this.requestLine = new RequestLine(bufferedReader.readLine());
        this.header = mapHeader(bufferedReader);
        this.body = mapBody(bufferedReader);
        this.httpCookie = new HttpCookie(header.get(HeaderName.COOKIE.getValue()));
    }

    private Map<String, String> mapHeader(BufferedReader bufferedReader) throws IOException {
        Map<String, String> header = new HashMap<>();
        String rawLine;

        while ((rawLine = bufferedReader.readLine()) != null && !rawLine.isEmpty()) {
            String[] headerEntry = rawLine.split(": ", 2);
            header.put(headerEntry[0], headerEntry[1]);
        }

        return header;
    }

    private String mapBody(BufferedReader bufferedReader) throws IOException {
        if (requestLine.isMethod(HttpMethod.POST)) {
            int contentLength = Integer.parseInt(get(HeaderName.CONTENT_LENGTH));
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength); // 어떻게 버퍼에 들어가는거지?
            return new String(buffer);
        }
        return "";
    }

    public boolean isMethod(HttpMethod httpMethod) {
        return requestLine.isMethod(httpMethod);
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String get(HeaderName headerName) {
        return header.get(headerName.getValue());
    }

    public boolean hasCookie() {
        return header.containsKey(HeaderName.COOKIE.getValue());
    }

    public String getQueryParam(String paramName) {
        return requestLine.getQueryParam(paramName);
    }

    public boolean isStaticRequest() {
        return requestLine.isStaticRequest();
    }

    public boolean isPath(String path) {
        return requestLine.isPath(path);
    }

    public boolean isPathWithQuery(String path) {
        return requestLine.isPathWithQuery(path);
    }
    public String getCookieResponse() {
        return httpCookie.getResponse();
    }

    public boolean hasESSIONID() {
       return httpCookie.hasJESSIONID();
    }

    public String getJESSIONID() {
        return httpCookie.getJESSIONID();
    }

    public HttpCookie getHttpCookie() {
        return httpCookie;
    }
}
