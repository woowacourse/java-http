package org.apache.coyote.http11.request;

import java.util.Map;
import org.apache.coyote.http11.RequestPathType;
import org.apache.coyote.http11.request.requestLine.HttpRequestLine;
import org.apache.coyote.http11.request.requestLine.MethodType;

public class HttpRequest {

    private static final String REQUEST_SEPARATOR = System.lineSeparator();

    private final HttpRequestLine httpRequestLine;
    private final HttpRequestHeader httpRequestHeader;
    private final HttpRequestBody httpRequestBody;

    public HttpRequest(HttpRequestLine httpRequestLine, HttpRequestHeader httpRequestHeader,
                       HttpRequestBody httpRequestBody) {
        this.httpRequestLine = httpRequestLine;
        this.httpRequestHeader = httpRequestHeader;
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

    //todo: 제거해야됨
    public Map<String, String> getRequestHeader() {
        return httpRequestHeader.getMap();
    }

    public Map<String, String> getRequestBody() {
        return httpRequestBody.getMap();
    }
}
