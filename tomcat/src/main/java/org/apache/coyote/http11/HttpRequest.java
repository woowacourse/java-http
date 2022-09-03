package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final String method;
    private final String uri;
    private final String version;
    private final String host;
    private final String accept;
    private final String connection;

    public static HttpRequest from(BufferedReader reader) throws IOException {
        String[] firstLine = reader.readLine().split(" ");
        String method = firstLine[0];
        String uri = firstLine[1];
        String version = firstLine[2];

        Map<String, String> headers = new HashMap<>();

        String header = reader.readLine();
        while (!header.isEmpty()) {
            String[] parsedHeader = header.split(" ");
            headers.put(parsedHeader[0], parsedHeader[1]);
            header = reader.readLine();
        }

        String host = headers.getOrDefault("Host:", "");
        String accept = headers.getOrDefault("Accept:", "");
        String connection = headers.getOrDefault("Connection:", "");

        return new HttpRequest(method, uri, version, host, accept, connection);
    }

    private HttpRequest(String method, String uri, String version, String host, String accept, String connection) {
        this.method = method;
        this.uri = uri;
        this.version = version;
        this.host = host;
        this.accept = accept;
        this.connection = connection;
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

}
