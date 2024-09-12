package org.apache.coyote.http11.request;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private static final String HEADER_SPLIT_DELIMITER = ": ";
    private static final String STATIC = "static";
    private static final String CSS = ".css";
    private static final String JS = ".js";
    private static final String HTML = ".html";

    private final HttpRequestLine httpRequestLine;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequest(HttpRequestLine httpRequestLine, Map<String, String> headers, String body) {
        this.httpRequestLine = httpRequestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        HttpRequestLine requestLine = HttpRequestLine.from(bufferedReader.readLine());

        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            String[] headerParts = line.split(HEADER_SPLIT_DELIMITER, 2);
            headers.put(headerParts[0], headerParts[1]);
        }

        if (requestLine.isPost()) {
            int contentLength = Integer.parseInt(headers.get(HttpHeaders.CONTENT_LENGTH));
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            String body = new String(buffer);
            return new HttpRequest(requestLine, headers, body);
        }

        return new HttpRequest(requestLine, headers, null);
    }

    public boolean isGet() {
        return httpRequestLine.isGet();
    }

    public boolean isPost() {
        return httpRequestLine.isPost();
    }

    public byte[] toHttpResponseBody() throws URISyntaxException, IOException {
        URL url = getClass().getClassLoader().getResource(loadResourceName(httpRequestLine.getPath()));
        if (url == null) {
            throw new IllegalArgumentException("존재하지 않는 리소스 입니다." + httpRequestLine.getPath());
        }

        Path path = Path.of(url.toURI());
        return Files.readAllBytes(path);
    }

    private String loadResourceName(String path) {
        if (path.equals("/")) {
            return STATIC + "/index.html";
        }
        if (!path.endsWith(HTML) && !path.contains(".")) {
            return STATIC + path + ".html";
        }
        return STATIC + path;
    }

    public String getContentType() {
        if (httpRequestLine.getPath().endsWith(CSS)) {
            return ContentType.CSS;
        }
        if (httpRequestLine.getPath().endsWith(JS)) {
            return ContentType.JAVASCRIPT;
        }
        return ContentType.HTML;
    }

    public HttpMethod getHttpMethod() {
        return httpRequestLine.getMethod();
    }

    public String getPath() {
        return httpRequestLine.getPath();
    }

    public String getVersion() {
        return httpRequestLine.getVersion();
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
