package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {

    private final HttpMethod method;
    private final String path;
    private final Map<String, String> query;
    private final String version;

    public RequestLine(HttpMethod method, String path, Map<String, String> query, String version) {
        this.method = method;
        this.path = path;
        this.query = query;
        this.version = version;
    }

    public static RequestLine from(String line){
        final String[] methodUri = line.split(" ");
        final HttpMethod method = HttpMethod.mapping(methodUri[0]);
        final String uri = methodUri[1];
        final Map<String,String> queries = readQueries(uri);
        final String path = readPath(uri);
        final String version = methodUri[2];
        return new RequestLine(method, path , queries, version);
    }

    private static String readPath(String uri){
        if(uri.contains("?")){
            return uri.split("\\?")[0];
        }
        return uri;
    }

    private static Map<String, String> readQueries(String uri) {
        if (!uri.contains("?")) {
            return new HashMap<>();
        }
        final Map<String, String> queriesMap = new HashMap<>();
        String[] queries = uri.split("\\?")[1].split("&");
        for (String query : queries) {
            String[] q = query.split("=");
            queriesMap.put(q[0], q[1]);
        }
        return queriesMap;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQuery() {
        return query;
    }
}
