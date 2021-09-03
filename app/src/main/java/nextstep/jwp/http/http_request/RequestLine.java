package nextstep.jwp.http.http_request;

public class RequestLine {

    private final JwpHttpMethod method;
    private final JwpPath path;
    private final String httpVersion;

    public RequestLine(String[] requestInfos) {
        this(JwpHttpMethod.of(requestInfos[0]), JwpPath.of(requestInfos[1]), requestInfos[2]);
    }

    public RequestLine(JwpHttpMethod method, JwpPath path, String httpVersion) {
        this.method = method;
        this.path = path;
        this.httpVersion = httpVersion;
    }

    public boolean isGetRequest() {
        return method.isGetRequest();
    }

    public boolean isPostRequest() {
        return method.isPostRequest();
    }

    public boolean hasQueryParams() {
        return path.hasQueryStrings();
    }

    public String getQueryParam(String account) {
        return path.getQueryParam(account);
    }

    public String getUri() {
        return path.getUri();
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
