package org.apache.coyote.http11.response;

import java.util.Map;
import java.util.stream.Collectors;

public class ResponseConverter {

    private final HttpResponse response;

    public ResponseConverter(final HttpResponse response) {
        this.response = response;
    }

    public String responseToFormat() {
        final HttpResponseBody responseBody = response.getResponseBody();
        final String body = responseBody.getBody();

        return String.join("\r\n",
                statusLineToFormat(),
                headersToFormat(),
                "",
                body);
    }

    private String statusLineToFormat() {
        final HttpResponseStatusLine statusLine = response.getStatusLine();
        final HttpStatusCode statusCode = statusLine.getStatusCode();

        return String.format("%s %d %s ",
                statusLine.getHttpVersion(),
                statusCode.getCode(),
                statusCode.getType());
    }

    private String headersToFormat() {
        final HttpResponseHeaders httpResponseHeaders = response.getHeader();
        final Map<ResponseHeaderType, Object> headers = httpResponseHeaders.getHeaders();

        return headers.entrySet().stream()
                .map(entry -> String.format("%s: %s ", entry.getKey().getType(), entry.getValue()))
                .collect(Collectors.joining("\r\n"));
    }
}
