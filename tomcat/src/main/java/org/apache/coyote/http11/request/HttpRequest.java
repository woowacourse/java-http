package org.apache.coyote.http11.request;

import java.io.IOException;
import java.io.InputStream;

public record HttpRequest(
        HttpMethod httpMethod,
        RequestUri requestUri,
        QueryParameters queryParameters
) {

    public static HttpRequest from(final InputStream inputStream) throws IOException {
        final RequestLine requestLine = RequestLine.from(inputStream);
        final RequestUri uri = RequestUri.from(requestLine.uri());
        final QueryParameters queryParameters = QueryParameters.from(uri.queryString());
        final HttpMethod httpMethod = HttpMethod.from(requestLine.method());

        return new HttpRequest(
                httpMethod,
                uri,
                queryParameters
        );
    }

    public HttpMethodType getMethodType() {
        return httpMethod.type();
    }

    public String getPath() {
        return requestUri.path();
    }

    public String getParameter(String key) {
        return queryParameters.getValueByKey(key);
    }
}
