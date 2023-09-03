package nextstep.jwp.http;

public class HttpRequest {

    private final HttpHeaders httpHeaders;
    private final HttpMethod httpMethod;
    private final HttpVersion httpVersion;
    private final HttpUri httpUri;
    private final HttpBody httpBody;

    public HttpRequest(
            HttpHeaders httpHeaders,
            HttpMethod httpMethod,
            HttpVersion httpVersion,
            HttpUri httpUri,
            HttpBody httpBody
    ) {
        this.httpHeaders = httpHeaders;
        this.httpMethod = httpMethod;
        this.httpVersion = httpVersion;
        this.httpUri = httpUri;
        this.httpBody = httpBody;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public HttpUri getHttpUri() {
        return httpUri;
    }

    public HttpBody getHttpBody() {
        return httpBody;
    }

    public String getNativePath() {
        return httpUri.getNativePath();
    }

}
