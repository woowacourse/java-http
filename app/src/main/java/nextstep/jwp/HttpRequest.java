package nextstep.jwp;

public class HttpRequest {

    private final String httpMethod;
    private final String uri;
    private final String httpVersion;

    public HttpRequest(String httpMethod, String uri, String httpVersion) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.httpVersion = httpVersion;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        return uri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
