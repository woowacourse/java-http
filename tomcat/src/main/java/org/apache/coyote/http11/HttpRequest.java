package org.apache.coyote.http11;

import java.net.URI;

public record HttpRequest(
        HttpMethod httpMethod,
        URI uri,
        HttpVersion httpVersion,
        Header header,
        char[] body
) {

    public static HttpRequest createHttp11Message(String requestLine, Header header, char[] requestBody) {
        String[] requestLines = requestLine.split(" ");
        HttpMethod httpMethod = HttpMethod.from(requestLines[0]);
        URI uri = URI.create(requestLines[1]);

        return new HttpRequest(httpMethod, uri, HttpVersion.HTTP_1_1, header, requestBody);
    }

    public boolean hasNotApplicationXW3FormUrlEncodedBody() {
        String requestContentType = header.get(HttpHeaderKey.CONTENT_TYPE)
                .orElse(ContentType.PLAIN.getName());
        ContentType contentType = ContentType.from(requestContentType);

        return !contentType.isApplicationXW3FormUrlEncoded();
    }
}
