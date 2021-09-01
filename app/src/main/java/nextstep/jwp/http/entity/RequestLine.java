package nextstep.jwp.http.entity;

public class RequestLine {
    private final HttpMethod httpMethod;
    private final HttpUri httpUri;
    private final HttpVersion httpVersion;

    public RequestLine(HttpMethod httpMethod, HttpUri httpUri, HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.httpUri = httpUri;
        this.httpVersion = httpVersion;
    }

    public static RequestLine of(String line) {
        String[] tokens = line.split(" ");
        HttpMethod httpMethod = HttpMethod.of(tokens[0]);
        HttpUri httpUri = HttpUri.of(tokens[1]);
        HttpVersion httpVersion = HttpVersion.of(tokens[2]);

        return new RequestLine(httpMethod, httpUri, httpVersion);
    }

    public HttpMethod httpMethod() {
        return httpMethod;
    }

    public HttpUri httpUri() {
        return httpUri;
    }

    public HttpVersion httpVersion() {
        return httpVersion;
    }
}
