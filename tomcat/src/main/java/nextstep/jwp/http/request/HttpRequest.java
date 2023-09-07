package nextstep.jwp.http.request;

import nextstep.jwp.http.common.HeaderType;
import nextstep.jwp.http.common.HttpBody;
import nextstep.jwp.http.common.HttpCookie;
import nextstep.jwp.http.common.HttpHeaders;
import nextstep.jwp.http.common.HttpVersion;

public class HttpRequest {

    private final HttpStartLine httpStartLine;
    private final HttpHeaders httpHeaders;
    private final HttpBody httpBody;

    public HttpRequest(
            HttpHeaders httpHeaders,
            HttpStartLine httpStartLine,
            HttpBody httpBody
    ) {
        this.httpStartLine = httpStartLine;
        this.httpHeaders = httpHeaders;
        this.httpBody = httpBody;
    }

    public boolean hasCookie() {
        return httpHeaders.containsKey(HeaderType.COOKIE.getValue());
    }

    public boolean hasQueryString() {
        return httpStartLine.hasQueryString();
    }

    public HttpMethod getHttpMethod() {
        return httpStartLine.getHttpMethod();
    }

    public HttpVersion getHttpVersion() {
        return httpStartLine.getHttpVersion();
    }

    public HttpBody getHttpBody() {
        return httpBody;
    }

    public String getNativePath() {
        return httpStartLine.getNativeUriPath();
    }

    public HttpCookie getCookie() {
        return HttpCookie.from(httpHeaders.get(HeaderType.COOKIE.getValue()));
    }

    public QueryString getQueryString() {
        return httpStartLine.getQueryString();
    }

}
