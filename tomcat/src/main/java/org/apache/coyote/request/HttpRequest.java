package org.apache.coyote.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.coockie.HttpCookie;
import org.apache.coyote.http.HeaderName;
import org.apache.coyote.http.HttpMethod;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> header;
    private final HttpCookie httpCookie;
    private final RequestBody body;

    public HttpRequest(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        this.requestLine = new RequestLine(bufferedReader.readLine());
        this.header = mapHeader(bufferedReader);
        this.body = mapBody(bufferedReader, header.get(HeaderName.CONTENT_LENGTH.getValue()));
        this.httpCookie = new HttpCookie(header.get(HeaderName.COOKIE.getValue()));
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

    public String getContentType() throws IOException {
        return requestLine.getContentType();
    }

    public String getCookieResponse() {
        return httpCookie.getResponse();
    }

    public boolean hasJESSIONID() {
        return httpCookie.hasJESSIONID();
    }

    public String getJESSIONID() {
        return httpCookie.getJESSIONID();
    }

    public HttpCookie getHttpCookie() {
        return httpCookie;
    }

    public RequestBody getBody() {
        return body;
    }

    private Map<String, String> mapHeader(BufferedReader bufferedReader) throws IOException {
        Map<String, String> rawHeader = new HashMap<>();
        String rawLine;

        while ((rawLine = bufferedReader.readLine()) != null && !rawLine.isEmpty()) {
            String[] headerEntry = rawLine.split(": ", 2);
            rawHeader.put(headerEntry[0], headerEntry[1]);
        }

        return rawHeader;
    }

    private RequestBody mapBody(BufferedReader bufferedReader, String bodyLength) throws IOException {
        if (requestLine.isMethod(HttpMethod.POST)) {
            return new RequestBody(bufferedReader, bodyLength);
        }
        return new RequestBody();
    }

    public boolean hasSession() {
        return hasCookie() && httpCookie.hasJESSIONID();
    }
}
