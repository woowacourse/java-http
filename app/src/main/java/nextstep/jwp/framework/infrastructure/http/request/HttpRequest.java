package nextstep.jwp.framework.infrastructure.http.request;

import nextstep.jwp.framework.infrastructure.http.method.HttpMethod;

public class HttpRequest {

    private final HttpRequestHeader httpRequestHeader;
    private final HttpRequestBody httpRequestBody;

    public HttpRequest(HttpRequestHeader httpRequestHeader, HttpRequestBody httpRequestBody) {
        this.httpRequestHeader = httpRequestHeader;
        this.httpRequestBody = httpRequestBody;
    }

    public HttpMethod getMethod() {
        return httpRequestHeader.getMethod();
    }

    public String getUrl() {
        return httpRequestHeader.getUrl();
    }
}
