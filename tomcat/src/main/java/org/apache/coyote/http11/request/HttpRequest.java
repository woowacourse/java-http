package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.HttpHeader.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.RequestPathType;
import org.apache.coyote.http11.request.requestLine.HttpRequestLine;
import org.apache.coyote.http11.request.requestLine.MethodType;

public class HttpRequest {

    private static final String COOKIE_SEPARATOR = "&";
    private static final String COOKIE_ENTRY_SEPARATOR = "=";

    private HttpRequestLine httpRequestLine;
    private HttpRequestHeader httpRequestHeader;
    private HttpRequestBody httpRequestBody;

    public HttpRequest(HttpRequestLine httpRequestLine, HttpRequestHeader httpRequestHeader,
                       HttpRequestBody httpRequestBody) {
        this.httpRequestLine = httpRequestLine;
        this.httpRequestHeader = httpRequestHeader;
        this.httpRequestBody = httpRequestBody;
    }

    public static HttpRequest createEmptyHttpRequest() {
        return new HttpRequest(null, null, null);
    }

    public void setHttpRequestLine(HttpRequestLine httpRequestLine) {
        this.httpRequestLine = httpRequestLine;
    }

    public void setHttpRequestHeader(HttpRequestHeader httpRequestHeader) {
        this.httpRequestHeader = httpRequestHeader;
    }

    public void setHttpRequestBody(HttpRequestBody httpRequestBody) {
        this.httpRequestBody = httpRequestBody;
    }

    public RequestPathType getRequestPathType() {
        return RequestPathType.reqeustPathToRequestPathType(httpRequestLine.getPath());
    }

    public String getRequestPath() {
        return httpRequestLine.getPath();
    }

    public MethodType getMethodType() {
        return httpRequestLine.getMethodType();
    }

    public Map<String, String> getRequestBody() {
        return httpRequestBody.getMap();
    }

    public List<HttpCookie> getCookies() {
        String cookies = httpRequestHeader.getValue(COOKIE.getHttpForm());
        return Arrays.stream(cookies.split(COOKIE_SEPARATOR))
                .map((singleHeader) -> singleHeader.split(COOKIE_ENTRY_SEPARATOR))
                .map((cookieEntry) -> new HttpCookie(cookieEntry[0], cookieEntry[1]))
                .collect(Collectors.toList());
    }

    public boolean isExistCookie() {
        return httpRequestHeader.isContainKey(COOKIE.getHttpForm());
    }
}
