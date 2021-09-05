package nextstep.joanne.server.http.request;

import nextstep.joanne.server.http.HttpMethod;

import java.util.Map;
import java.util.Objects;

public class RequestLine {
    private final HttpMethod httpMethod;
    private final String uri;
    private final Map<String, String> queryString;
    private final String version;

    public RequestLine(HttpMethod httpMethod, String uri, Map<String, String> queryString) {
        this(httpMethod, uri, queryString, "HTTP/1.1");
    }

    public RequestLine(HttpMethod httpMethod, String uri, Map<String, String> queryString, String version) {
        this.httpMethod = httpMethod;
        this.uri = checkDefault(uri);
        this.queryString = queryString;
        this.version = version;
    }

    private String checkDefault(String uri) {
        if (Objects.equals(uri, "/")) {
            return "/index.html";
        }
        return uri;
    }

    public HttpMethod httpMethod() {
        return httpMethod;
    }

    public String uri() {
        return uri;
    }

    public Map<String, String> queryString() {
        return queryString;
    }

    public String version() {
        return version;
    }

    public boolean isSameHttpMethod(HttpMethod method) {
        return httpMethod.sameWith(method);
    }
}
