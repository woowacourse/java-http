package nextstep.jwp.http;

public class Request {

    private final RequestInfo requestInfo;
    private final Headers headers;
    private final String content;

    public Request(final RequestInfo requestInfo, final Headers headers, final String content) {
        this.requestInfo = requestInfo;
        this.headers = headers;
        this.content = content;
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

    public String getContent() {
        return content;
    }
}
