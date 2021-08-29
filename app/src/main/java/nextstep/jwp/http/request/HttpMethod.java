package nextstep.jwp.http.request;

public class HttpMethod {

    private final String method;

    public HttpMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public boolean isGet() {
        return "GET".equals(method);
    }

    public boolean isPost() {
        return "POST".equals(method);
    }
}
