package org.apache.coyote.http11.request;

import java.io.IOException;
import java.io.InputStream;

public record HttpRequest(
        HttpMethod httpMethod,
        RequestUri requestUri,
        QueryParameters queryParameters
) {

    public static HttpRequest from(final InputStream inputStream) throws IOException {
        RequestLine requestLine = RequestLine.from(inputStream);
        RequestUri uri = RequestUri.from(requestLine.getUri());
        QueryParameters queryParameters = QueryParameters.from(uri.getQueryString());

        return new HttpRequest(
                HttpMethod.from(requestLine.getMethod()),
                uri,
                queryParameters
        );
    }

    public HttpMethodType getMethodType() {
        return httpMethod.type();
    }

    public String getPath() {
        return requestUri.getPath();
    }

    public String getParameter(String key) {
        return queryParameters.getValueByKey(key);
    }
}
