package nextstep.jwp.framework.infrastructure.http.request;

import java.util.Map;
import nextstep.jwp.framework.infrastructure.http.method.HttpMethod;
import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;
import nextstep.jwp.framework.infrastructure.protocol.Protocol;

public class HttpRequest {

    private static final String DEFAULT_HTTP_STATUS_HTML_FORMAT = "/%s.html";

    private final HttpRequestHeader httpRequestHeader;
    private final HttpRequestBody httpRequestBody;

    public HttpRequest(HttpRequestHeader httpRequestHeader, HttpRequestBody httpRequestBody) {
        this.httpRequestHeader = httpRequestHeader;
        this.httpRequestBody = httpRequestBody;
    }

    public static HttpRequest ofStaticFile(HttpStatus httpStatus) {
        return ofStaticFile(String.format(DEFAULT_HTTP_STATUS_HTML_FORMAT, httpStatus.getCode()));
    }

    public static HttpRequest ofStaticFile(String resourcePath) {
        return new HttpRequest(new HttpRequestHeader(resourcePath), new HttpRequestBody(null));
    }

    public Map<String, String> getContentAsAttributes() {
        return httpRequestBody.getContentAsAttributes();
    }

    public HttpMethod getMethod() {
        return httpRequestHeader.getMethod();
    }

    public String getUrl() {
        return httpRequestHeader.getUrl();
    }

    public Protocol getProtocol() {
        return httpRequestHeader.getProtocol();
    }
}
