package nextstep.joanne.http.request;

import nextstep.joanne.http.HttpMethod;

import java.util.Map;

public class RequestLine {
    private final HttpMethod httpMethod;
    private final String uri;
    private final Map<String, String> queryString;
    private final String version;

    public RequestLine(HttpMethod httpMethod, String uri, Map<String, String> queryString, String version) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.queryString = queryString;
        this.version = version;
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
}
