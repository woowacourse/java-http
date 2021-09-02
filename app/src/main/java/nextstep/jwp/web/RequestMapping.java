package nextstep.jwp.web;

import nextstep.jwp.request.HttpMethod;
import nextstep.jwp.request.RequestLine;

import java.util.Objects;

public class RequestMapping {

    private final String path;
    private final HttpMethod httpMethod;

    public RequestMapping(String path, HttpMethod httpMethod) {
        this.path = path;
        this.httpMethod = httpMethod;
    }

    public static RequestMapping of(RequestLine requestLine) {
        return new RequestMapping(requestLine.getRequestPath(), requestLine.getHttpMethod());
    }

    public String getPath() {
        return path;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestMapping that = (RequestMapping) o;
        return Objects.equals(path, that.path) && httpMethod == that.httpMethod;
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, httpMethod);
    }
}
