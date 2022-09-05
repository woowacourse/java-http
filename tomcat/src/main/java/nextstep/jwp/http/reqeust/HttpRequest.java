package nextstep.jwp.http.reqeust;

import java.util.Map;
import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.HttpHeader;

public class HttpRequest {

    private final HttpRequestLine httpRequestLine;
    private final HttpHeader httpHeaders;
    private final HttpRequestBody httpRequestBody;

    public HttpRequest(final HttpRequestLine httpRequestLine, final HttpHeader httpHeaders,
                       final HttpRequestBody httpRequestBody) {
        this.httpRequestLine = httpRequestLine;
        this.httpHeaders = httpHeaders;
        this.httpRequestBody = httpRequestBody;
    }

    public String findContentType() {
        String url = getPath();
        return ContentType.findContentType(url);
    }

    public boolean hasQueryParams() {
        return httpRequestLine.hasQueryParams();
    }

    public String getPath() {
        return httpRequestLine.getPath();
    }

    public Map<String, String> getQueryParams() {
        return httpRequestLine.getQueryParams();
    }

    public String getMethod() {
        return httpRequestLine.getMethod();
    }

    public Map<String, String> getRequestBodies() {
        return httpRequestBody.getRequestBodies();
    }
}
