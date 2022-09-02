package nextstep.jwp.http.reqeust;

import java.util.Map;
import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.HttpHeader;

public class HttpRequest {

    private final HttpRequestLine httpRequestLine;
    private final HttpHeader httpHeaders;

    public HttpRequest(final HttpRequestLine httpRequestLine, final HttpHeader httpHeaders) {
        this.httpRequestLine = httpRequestLine;
        this.httpHeaders = httpHeaders;
    }

    public String findContentType() {
        String url = getPath();
        return ContentType.findContentType(url);
    }

    public boolean hasQueryString() {
        return httpRequestLine.hasQueryParams();
    }

    public String getPath() {
        return httpRequestLine.getPath();
    }

    public Map<String, String> getQueryParams() {
        return httpRequestLine.getQueryParams();
    }
}
