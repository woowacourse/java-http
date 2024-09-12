package org.apache.coyote.http11.request;

public record HttpRequest(
        HttpRequestHeader header,
        HttpRequestBody body) {

    public String getQueryStringValue(String key) {
        return header.getQueryStringValue(key);
    }
}
