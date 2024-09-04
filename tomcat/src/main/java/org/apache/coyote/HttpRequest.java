package org.apache.coyote;

import java.util.Map;

public class HttpRequest {

    private final String httpMethod;
    private final String path;
    private final String version;
    private final Map<String, String> headers;

    public HttpRequest(String request) {
        String[] lines = request.split("\r\n");
        String[] requestLine = lines[0].split(" ");
        this.httpMethod = requestLine[0];
        this.path = requestLine[1];
        this.version = requestLine[2];
        this.headers = Map.of(
                "Host", lines[1].split(": ")[1],
                "Connection", lines[2].split(": ")[1]
        );
    }

    public String getPath() {
        return path;
    }
}
