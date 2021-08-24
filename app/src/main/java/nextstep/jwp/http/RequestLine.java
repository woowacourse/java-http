package nextstep.jwp.http;

public class RequestLine {
    private final HttpMethod method;
    private final String requestUri;
    private final String httpVersion;

    public static RequestLine of(String requestLine) {
        if (requestLine == null) {
            throw new IllegalArgumentException("RequestLine의 형식이 올바르지 않습니다.");
        }
        String[] splitLines = requestLine.split(" ");
        if (splitLines.length != 3) {
            throw new IllegalArgumentException("RequestLine의 형식이 올바르지 않습니다.");
        }
        return new RequestLine(HttpMethod.of(splitLines[0]), splitLines[1], splitLines[2]);
    }

    public RequestLine(HttpMethod method, String requestUri, String httpVersion) {
        this.method = method;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }

    public String getRequestUri() {
        return requestUri;
    }
}
