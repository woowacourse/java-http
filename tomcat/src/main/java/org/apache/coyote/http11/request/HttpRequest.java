package org.apache.coyote.http11.request;

import java.util.Map;
import org.apache.coyote.http11.RequestPathType;
import org.apache.coyote.http11.request.requestLine.HttpRequestLine;
import org.apache.coyote.http11.request.requestLine.MethodType;

public class HttpRequest {

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

}
