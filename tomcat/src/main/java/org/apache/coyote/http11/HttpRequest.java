package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    private final String method;
    private final String uri;
    private final String version;
    private final String host;
    private final String accept;
    private final String connection;

    public static HttpRequest from(List<String> input) {
        String firstLine = input.get(0);
        HttpRequestStartLine startLine = HttpRequestStartLine.from(firstLine);

        Map<String, String> headers = new HashMap<>();
        for (String header : input) {
            String[] parsedHeader = header.split(": ");
            headers.put(parsedHeader[0], parsedHeader[1]);
        }

        return new HttpRequest(
            startLine.getMethod(),
            startLine.getUri(),
            startLine.getVersion(),
            headers.get("Host"),
            headers.get("Accept"),
            headers.get("Connection")
        );
    }

    private HttpRequest(String method, String uri, String version, String host, String accept, String connection) {
        this.method = method;
        this.uri = uri;
        this.version = version;
        this.host = host;
        this.accept = accept;
        this.connection = connection;
    }

    public boolean isGet() {
        return method.equals("GET");
    }

    public boolean isPost() {
        return method.equals("POST");
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getVersion() {
        return version;
    }

    public String getHost() {
        return host;
    }

    public String getAccept() {
        return accept;
    }

    public String getConnection() {
        return connection;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
            "method='" + method + '\'' +
            ", uri='" + uri + '\'' +
            ", version='" + version + '\'' +
            ", host='" + host + '\'' +
            ", accept='" + accept + '\'' +
            ", connection='" + connection + '\'' +
            '}';
    }
}
