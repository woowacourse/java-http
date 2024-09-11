package org.apache.catalina.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.catalina.http.body.HttpRequestBody;
import org.apache.catalina.http.header.HttpHeader;
import org.apache.catalina.http.header.HttpHeaders;
import org.apache.catalina.http.startline.HttpMethod;
import org.apache.catalina.http.startline.HttpRequestLine;
import org.apache.catalina.http.startline.HttpVersion;
import org.apache.catalina.session.Session;

public class HttpRequest {

    private final HttpRequestLine httpRequestLine;
    private final HttpHeaders httpHeaders;
    private final HttpRequestBody httpRequestBody;

    public HttpRequest(HttpRequestLine httpRequestLine, HttpHeaders httpHeaders, HttpRequestBody httpRequestBody) {
        this.httpRequestLine = httpRequestLine;
        this.httpHeaders = httpHeaders;
        this.httpRequestBody = httpRequestBody;
    }

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        BufferedReader httpRequestReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequestLine startLine = HttpRequestLine.parse(httpRequestReader.readLine());

        HttpHeaders httpHeaders = getHttpHeaders(httpRequestReader);

        if (!httpRequestReader.ready()) {
            return new HttpRequest(startLine, httpHeaders, new HttpRequestBody());
        }
        HttpRequestBody requestBody = getRequestBody(httpHeaders, httpRequestReader);
        return new HttpRequest(startLine, httpHeaders, requestBody);
    }

    private static HttpHeaders getHttpHeaders(BufferedReader httpRequestReader) throws IOException {
        List<String> headers = new ArrayList<>();
        String header;
        while (Objects.nonNull(header = httpRequestReader.readLine()) && !header.isEmpty()) {
            headers.add(header);
        }
        return HttpHeaders.parse(headers);
    }

    private static HttpRequestBody getRequestBody(HttpHeaders httpHeaders, BufferedReader httpRequestReader)
            throws IOException {
        int contentLength = Integer.parseInt(httpHeaders.get(HttpHeader.CONTENT_LENGTH));
        char[] buffer = new char[contentLength];
        httpRequestReader.read(buffer, 0, contentLength);
        return HttpRequestBody.parseUrlEncoded(new String(buffer));
    }

    public boolean isURIStatic() {
        return httpRequestLine.isURIStatic();
    }

    public boolean isURIHome() {
        return httpRequestLine.isURIHome();
    }

    public boolean URIStartsWith(String startsWith) {
        return httpRequestLine.URIStartsWith(startsWith);
    }

    public Optional<String> getSessionFromCookies() {
        return httpHeaders.getFromCookies(Session.KEY);
    }

    public HttpMethod getHttpMethod() {
        return httpRequestLine.getHttpMethod();
    }

    public Path getTargetPath() {
        return httpRequestLine.getTargetPath();
    }

    public String getFromBody(String key) {
        return httpRequestBody.get(key);
    }

    public HttpVersion getHttpVersion() {
        return httpRequestLine.getHttpVersion();
    }
}
