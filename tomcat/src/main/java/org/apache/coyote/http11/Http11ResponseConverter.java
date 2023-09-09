package org.apache.coyote.http11;

import org.apache.coyote.response.HttpResponse;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Http11ResponseConverter {

    private static final String SPACE = " ";
    private static final String CRLF = "\r\n";

    private Http11ResponseConverter() {
    }

    public static byte[] convertToBytes(final HttpResponse response) {
        final StringBuilder responseBuilder = new StringBuilder();
        appendResponseLine(response, responseBuilder);
        appendResponseHeader(response, responseBuilder);
        appendResponseBody(response, responseBuilder);

        return responseBuilder.toString().getBytes(UTF_8);
    }

    private static void appendResponseLine(final HttpResponse response, final StringBuilder responseBuilder) {
        responseBuilder
                .append(response.httpVersion().version()).append(SPACE)
                .append(response.httpStatus().statusCode()).append(SPACE)
                .append(response.httpStatus().statusName()).append(SPACE)
                .append(CRLF);
    }

    private static void appendResponseHeader(final HttpResponse response, final StringBuilder responseBuilder) {
        for (String headerName : response.headerNames()) {
            final String headerValue = response.getHeaderValue(headerName);

            responseBuilder
                    .append(headerName).append(": ").append(headerValue).append(SPACE)
                    .append(CRLF);
        }
    }

    private static void appendResponseBody(final HttpResponse response, final StringBuilder responseBuilder) {
        responseBuilder
                .append(CRLF)
                .append(response.responseBody().body());
    }
}
