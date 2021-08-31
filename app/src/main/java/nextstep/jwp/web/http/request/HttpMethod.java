package nextstep.jwp.web.http.request;

public enum HttpMethod {
    GET,
    HEAD,
    POST,
    PUT,
    DELETE,
    CONNECT,
    OPTIONS,
    TRACE,
    PATCH;

    HttpMethod() {
    }

    public static HttpMethod getMethod(String method) {
        return HttpMethod.valueOf(method);
    }

    public boolean isGet() {
        return this.equals(HttpMethod.GET);
    }

    public boolean isPost() {
        return this.equals(HttpMethod.POST);
    }
}
