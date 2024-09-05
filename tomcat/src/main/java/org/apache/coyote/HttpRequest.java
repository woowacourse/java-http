package org.apache.coyote;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private static final String CRLF = "\r\n";

    private final String httpMethod;
    private final String path;
    private final String version;
    private final Map<String, String> headers;

    public HttpRequest(String request) {
        String[] lines = request.split(CRLF);
        String[] requestLine = lines[0].split(" ");
        this.httpMethod = requestLine[0];
        this.path = requestLine[1];
        this.version = requestLine[2];
        this.headers = parseHeaders(lines);
    }

    private Map<String, String> parseHeaders(String[] lines) {
        Map<String, String> result = new HashMap<>();
        for (int i = 1; i < lines.length; i++) {
            String[] header = lines[i].split(": ");
            result.put(header[0], header[1]);
        }
        return result;
    }

    public String getPath() {
        return path;
    }

    public String getAccept() {
        return headers.getOrDefault("Accept", "*/*");
    }
}
