package nextstep.jwp.http.request;

import nextstep.jwp.http.common.HttpUri;
import nextstep.jwp.http.common.HttpVersion;

public class HttpStartLine {

    private final HttpMethod httpMethod;
    private final HttpUri httpUri;
    private final HttpVersion httpVersion;

    public HttpStartLine(HttpMethod httpMethod, HttpUri httpUri, HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.httpUri = httpUri;
        this.httpVersion = httpVersion;
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

    public String getNativeUriPath() {
        return httpUri.getNativePath();
    }

    public QueryString getQueryString() {
        return httpUri.getQueryString();
    }

    public boolean hasQueryString() {
        return httpUri.hasQueryString();
    }

}
