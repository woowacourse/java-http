package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private static final String HEADER_SPLIT_DELIMITER = ": ";

    private final String method;
    private final String path;
    private final String version;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequest(String method, String path, String version, Map<String, String> headers, String body) {
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
        String method = parsedStartLine[0];
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

        if (method.equals("POST")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            String body = new String(buffer);
            return new HttpRequest(method, path, version, headers, body);
        }

        return new HttpRequest(method, path, version, headers, null);
    }

    public String getHttpMethod() {
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
