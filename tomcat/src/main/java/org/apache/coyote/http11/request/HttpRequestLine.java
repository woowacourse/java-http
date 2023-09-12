package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestLine {

    private final String method;
    private final String target;
    private final Map<String, String> queries;
    private final String version;

    private HttpRequestLine(final String method, final String target, final Map<String, String> queries, final String version) {
        this.method = method;
        this.target = target;
        this.queries = queries;
        this.version = version;
    }

    public static HttpRequestLine from(final String method, final String target, final String version) {
        return new HttpRequestLine(method, target, new HashMap<>(), version);
    }

    public static HttpRequestLine from(final String method, final String target, final Map<String, String> queries, final String version) {
        return new HttpRequestLine(method, target, queries, version);
    }

    public String getMethod() {
        return method;
    }

    public String getTarget() {
        return target;
    }

    public Map<String, String> getQueries() {
        return queries;
    }

    public String getVersion() {
        return version;
    }
}
