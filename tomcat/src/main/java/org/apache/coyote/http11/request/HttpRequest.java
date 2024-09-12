package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.request.HttpMethod.*;
import static org.apache.coyote.http11.request.HttpMethod.GET;

public record HttpRequest(
        HttpRequestLine requestLine,
        HttpRequestHeader header,
        HttpRequestBody body) {

    public boolean isGET() {
        return GET.equals(this.requestLine.httpMethod());
    }

    public boolean isPOST() {
        return POST.equals(this.requestLine.httpMethod());
    }
}
