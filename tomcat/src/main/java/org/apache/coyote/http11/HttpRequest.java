package org.apache.coyote.http11;

import java.net.URI;

public record HttpRequest(String startLine, Header header, char[] body) {

    public HttpMethod getMethod() {
        return HttpMethod.from(startLine.split(" ")[0]);
    }

    public URI getUri() {
        return URI.create(startLine.split(" ")[1]);
    }

    public HttpVersion getHttpVersion() {
        return HttpVersion.from(startLine.split(" ")[2]);
    }

    public QueryParameter getQueryParameter() {
        return new QueryParameter(getUri().getQuery());
    }

    public boolean hasNotApplicationXW3FormUrlEncodedBody() {
        String requestContentType = header.get(HttpHeaderKey.CONTENT_TYPE)
                .orElse(ContentType.PLAIN.getName());
        ContentType contentType = ContentType.from(requestContentType);

        return !contentType.isApplicationXW3FormUrlEncoded();
    }
}
