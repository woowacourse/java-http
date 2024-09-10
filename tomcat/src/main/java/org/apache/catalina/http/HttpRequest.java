package org.apache.catalina.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.catalina.http.body.HttpRequestBody;
import org.apache.catalina.http.header.HttpHeader;
import org.apache.catalina.http.header.HttpHeaders;
import org.apache.catalina.http.startline.HttpMethod;
import org.apache.catalina.http.startline.HttpRequestLine;

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

        List<String> headers = new ArrayList<>();
        while (httpRequestReader.ready()) {
            String header = httpRequestReader.readLine();
            if (header.isBlank()) {
                break;
            }
            headers.add(header);
        }
        HttpHeaders httpHeaders = HttpHeaders.parse(headers);

        if (!httpRequestReader.ready()) {
            return new HttpRequest(startLine, httpHeaders, HttpRequestBody.empty());
        }
        int contentLength = Integer.parseInt(httpHeaders.get(HttpHeader.CONTENT_LENGTH));
        char[] buffer = new char[contentLength];
        httpRequestReader.read(buffer, 0, contentLength);
        HttpRequestBody requestBody = HttpRequestBody.parseUrlEncoded(new String(buffer));
        return new HttpRequest(startLine, httpHeaders, requestBody);
    }

    public boolean isTargetStatic() {
        return httpRequestLine.isTargetStatic();
    }

    public boolean isTargetBlank() {
        return httpRequestLine.isTargetBlank();
    }

    public boolean uriStartsWith(String startsWith) {
        return httpRequestLine.uriStartsWith(startsWith);
    }

    public Optional<String> getSessionFromCookie() {
        return httpHeaders.getSessionFromCookie();
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

    public String getHttpVersion() {
        return httpRequestLine.getHttpVersion();
    }
}
