package org.apache.coyote.http11;

public record HttpRequest(
        HttpRequestHeader requestHeader,
        HttpRequestBody requestBody
) {
}
