package nextstep.jwp.web.http.request;

public class StartLine {

    private HttpMethod method;
    private RequestTarget requestTarget;
    private String httpVersion;

    private StartLine() {
    }

    public StartLine(String rawStartLine) {
        String[] parsedRawStartLine = rawStartLine.split(" ");
        this.method = HttpMethod.getMethod(parsedRawStartLine[0]);
        this.requestTarget = new RequestTarget(parsedRawStartLine[1]);
        this.httpVersion = parsedRawStartLine[2];
    }

    public String getUrl() {
        return requestTarget.getUrl();
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestParams(String key) {
        return requestTarget.getRequestParams(key);
    }
}
