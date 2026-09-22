package org.apache.coyote.http11.request;

public record HttpRequest(
        HttpRequestHeader requestHeader,
        HttpRequestBody requestBody
) {
}
