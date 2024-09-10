package org.apache.coyote.http11;

import java.net.URI;

public record HttpRequest(
        HttpMethod httpMethod,
        URI uri,
        HttpVersion httpVersion,
        Header header,
        HttpBody body
) {

    public static HttpRequest createHttp11Message(String requestLine, Header header, HttpBody requestBody) {
        String[] requestLines = requestLine.split(" ");
        HttpMethod httpMethod = HttpMethod.from(requestLines[0]);
        URI uri = URI.create(requestLines[1]);

        return new HttpRequest(httpMethod, uri, HttpVersion.HTTP_1_1, header, requestBody);
    }
}
