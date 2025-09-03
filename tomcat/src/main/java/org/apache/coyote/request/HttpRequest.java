package org.apache.coyote.request;

import org.apache.coyote.request.requestInfo.RequestInfo;

public class HttpRequest {

    private final RequestInfo requestInfo;
    private final RequestHeader requestHeader;
    private final RequestBody requestBody;

    public HttpRequest(final RequestInfo requestInfo, final RequestHeader requestHeader, final RequestBody requestBody) {
        this.requestInfo = requestInfo;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public boolean isDefaultRequestPath() {
        return requestInfo.isDefaultPath();
    }

    public RequestInfo getRequestInfo() {
        return requestInfo;
    }

    public String getRequestPath(){
        return requestInfo.getRequestPath();
    }
}
