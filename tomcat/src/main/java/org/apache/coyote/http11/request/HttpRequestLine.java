package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

public class HttpRequestLine {
    private final HttpMethod method;
    private final String location;
    private final String version;
    private final Map<String, String> queries = new TreeMap<>();

    public HttpRequestLine(String method, String location, String version) {
        this.method = HttpMethod.from(method);
        this.location = location.split("\\?")[0];
        this.version = version;
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
        try {
            String requestLine = clientData.getFirst();
            List<String> split = Arrays.stream(requestLine.split(" "))
                    .map(String::strip)
                    .toList();
            validate(split);
            return new HttpRequestLine(split.get(0), split.get(1), split.get(2));
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("data parse error at request line");
        }
    }

    private static void validate(List<String> requestLine) {
        // TODO: validate
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getLocation() {
        return location;
    }

    public String getVersion() {
        return version;
    }

    public Map<String,String> getQueries() {
        return queries;
    }
}
