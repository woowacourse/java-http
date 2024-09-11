package org.apache.coyote.http11.request;

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

    private final HttpMethod method;
    private final String path;
    private final String version;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequest(HttpMethod method, String path, String version, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String startLine = bufferedReader.readLine();

        String[] parsedStartLine = HttpRequestParser.parseStartLine(startLine);
        HttpMethod method = HttpMethod.from(parsedStartLine[0]);
        String path = parsedStartLine[1];
        String version = parsedStartLine[2];

        Map<String, String> headers = new HashMap<>();
        while (!startLine.isEmpty()) {
            String line = bufferedReader.readLine();
            if (line.isEmpty()) {
                break;
            }
            String[] headerParts = line.split(HEADER_SPLIT_DELIMITER, 2);
            headers.put(headerParts[0], headerParts[1]);
        }

        if (method.isPost()) {
            int contentLength = Integer.parseInt(headers.get(HttpHeaders.CONTENT_LENGTH));
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            String body = new String(buffer);
            return new HttpRequest(method, path, version, headers, body);
        }

        return new HttpRequest(method, path, version, headers, null);
    }

    public boolean isGet() {
        return method.isGet();
    }

    public boolean isPost() {
        return method.isPost();
    }

    public byte[] toHttpResponseBody() throws URISyntaxException, IOException {
        URL url = getClass().getClassLoader().getResource(loadResourceName(path));
        if (url == null) {
            throw new IllegalArgumentException("존재하지 않는 리소스 입니다." + path);
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
        if (path.endsWith(CSS)) {
            return "text/css";
        }
        if (path.endsWith(JS)) {
            return "text/javascript";
        }
        return "text/html;charset=utf-8";
    }

    public HttpMethod getHttpMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
