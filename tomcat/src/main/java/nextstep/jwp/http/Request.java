package nextstep.jwp.http;

public class Request {

    private final RequestInfo requestInfo;
    private final Headers headers;
    private final String body;

    public Request(final RequestInfo requestInfo, final Headers headers, final String body) {
        this.requestInfo = requestInfo;
        this.headers = headers;
        this.body = body;
    }

    public String getUri() {
        return requestInfo.getUri();
    }

    public String getQueryString() {
        return requestInfo.getQueryString();
    }

    public RequestInfo getRequestInfo() {
        return requestInfo;
    }

    public Headers getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
