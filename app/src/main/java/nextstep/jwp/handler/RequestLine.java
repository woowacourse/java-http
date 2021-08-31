package nextstep.jwp.handler;

public class RequestLine {
    private final HttpMethod method;
    private final RequestUrl requestUrl;
    private final String version;

    public RequestLine(HttpMethod method, RequestUrl path, String version) {
        this.method = method;
        this.requestUrl = path;
        this.version = version;
    }

    public boolean hasFileSuffixUri() {
        return requestUrl.getPath().contains(".");
    }

    public boolean hasQueryParams() {
        return requestUrl.getUrl().contains("?");
    }

    public boolean isSameMethodAs(HttpMethod method) {
        return this.method == method;
    }

    public RequestUrl getRequestUrl() {
        return requestUrl;
    }

    public String getVersion() {
        return version;
    }
}
