package org.apache.coyote.http11.response;

import org.apache.coyote.http11.mapping.HandlerMapping;
import org.apache.coyote.http11.mapping.MappingResponse;
import org.apache.coyote.http11.request.HttpRequest;

public class HttpResponseGenerator {

    private HttpResponseGenerator() {
    }

    public static HttpResponse createHttpResponse(final HttpRequest httpRequest) {
        final MappingResponse response = getResourceFromUrl(httpRequest.getUrl());
        final String resource = response.getResource();
        final String statusCode = response.getStatusCode();

        return HttpResponse.of(httpRequest.getHttpVersion(), resource, statusCode);
    }

    private static MappingResponse getResourceFromUrl(final String url) {
        return HandlerMapping.getInstance()
            .getResponse(url);
    }
}
