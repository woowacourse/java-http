package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HttpRequestLine {
    private final HttpMethod method;
    private final HttpLocation location;
    private final HttpVersion version;
    private final Map<String, String> queries = new TreeMap<>();

    private HttpRequestLine(String method, String location, String version) {
        this.method = HttpMethod.from(method);
        this.location = HttpLocation.from(location);
        this.version = HttpVersion.from(version);
        setQueriesIfExist(location);
    }

    private void setQueriesIfExist(String location) {
        String[] split = location.split("\\?");
        if (split.length > 1) {
            for (int i = 1; i < split.length; i++) {
                String[] keyValue = split[i].split("=");
                queries.put(keyValue[0], keyValue[1]);
            }
        }
    }

    public static HttpRequestLine from(List<String> clientData) {
        if (clientData.isEmpty()) {
            throw new IllegalArgumentException("data parse error at request line");
        }

        String requestLine = clientData.getFirst();
        List<String> split = Arrays.stream(requestLine.split(" ")).toList();
        if (split.size() != 3) {
            throw new IllegalArgumentException("request line must have 3 arguments");
        }

        return new HttpRequestLine(split.get(0), split.get(1), split.get(2));

    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpLocation getLocation() {
        return location;
    }

    public Map<String, String> getQueries() {
        return queries;
    }
}
