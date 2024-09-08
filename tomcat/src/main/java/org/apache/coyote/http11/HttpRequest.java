package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.coyote.http11.body.HttpRequestBody;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.header.HttpHeaders;
import org.apache.coyote.http11.startline.HttpMethod;
import org.apache.coyote.http11.startline.HttpRequestLine;

public class HttpRequest {

    private final HttpRequestLine httpRequestLine;
    private final HttpHeaders httpHeaders;
    private final HttpRequestBody httpRequestBody;

    private HttpRequest(HttpRequestLine httpRequestLine, HttpHeaders httpHeaders, HttpRequestBody httpRequestBody) {
        this.httpRequestLine = httpRequestLine;
        this.httpHeaders = httpHeaders;
        this.httpRequestBody = httpRequestBody;
    }

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        BufferedReader httpRequestReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequestLine startLine = new HttpRequestLine(httpRequestReader.readLine());

        List<String> headers = new ArrayList<>();
        while (httpRequestReader.ready()) {
            String header = httpRequestReader.readLine();
            if (header.isBlank()) {
                break;
            }
            headers.add(header);
        }
        HttpHeaders httpHeaders = new HttpHeaders(headers);

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

    public boolean targetStartsWith(String startsWith) {
        return httpRequestLine.targetStartsWith(startsWith);
    }

    public boolean targetEqualTo(String target) {
        return httpRequestLine.targetEqualTo(target);
    }

    public boolean containsBody() {
        return httpRequestBody.isNotEmpty();
    }

    public Optional<String> getSessionFromCookie() {
        return httpHeaders.getSessionFromCookie();
    }

    public HttpMethod getHttpMethod() {
        return httpRequestLine.getHttpMethod();
    }

    public String getTargetExtension() {
        return httpRequestLine.getTargetExtension();
    }

    public Path getPath() {
        URL resource = httpRequestLine.getTargetUrl();
        try {
            return Path.of(resource.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("cannot convert to URI: " + resource);
        }
    }

    public String getFromBody(String key) {
        return httpRequestBody.get(key);
    }

    public String getHttpVersion() {
        return httpRequestLine.getHttpVersion();
    }
}
