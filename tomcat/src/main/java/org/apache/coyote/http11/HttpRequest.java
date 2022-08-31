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
        String[] lines = reader.readLine().split(" ");
        String method = lines[0];
        String uri = lines[1];
        String version = lines[2];

        Map<String, String> map = new HashMap<>();

        String line = reader.readLine();
        while (!line.isEmpty()) {
            lines = line.split(" ");
            map.put(lines[0], lines[1]);
            line = reader.readLine();
        }

        String host = map.getOrDefault("Host:", "");
        String accept = map.getOrDefault("Accept:", "");
        String connection = map.getOrDefault("Connection:", "");

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
