package org.apache.coyote.http11.request;

public record HttpRequest(
        HttpRequestHeader header,
        HttpRequestBody body) {

    public boolean isMethodGET() {
        return HttpMethod.GET.equals(header.requestLine().httpMethod());
    }

    public boolean isMethodPOST() {
        return HttpMethod.POST.equals(header.requestLine().httpMethod());
    }
}
