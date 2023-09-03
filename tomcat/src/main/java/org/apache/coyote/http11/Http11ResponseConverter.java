package org.apache.coyote.http11;

import org.apache.coyote.response.HttpResponse;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Http11ResponseConverter {

    private static final String SPACE = " ";
    private static final String ENTER = "\r\n";

    private Http11ResponseConverter() {
    }

    public static byte[] convertToBytes(final HttpResponse response) {
        final StringBuilder responseByteBuilder = new StringBuilder();
        appendResponseLine(response, responseByteBuilder);
        appendResponseHeaders(response, responseByteBuilder);
        appendResponseBody(response, responseByteBuilder);

        return responseByteBuilder.toString().getBytes(UTF_8);
    }

    private static void appendResponseLine(final HttpResponse response, final StringBuilder responseByteBuilder) {
        responseByteBuilder
                .append(response.httpVersion().version()).append(SPACE)
                .append(response.httpStatus().statusCode()).append(SPACE)
                .append(response.httpStatus().statusName()).append(SPACE)
                .append(ENTER);
    }

    private static void appendResponseHeaders(final HttpResponse response, final StringBuilder responseByteBuilder) {
        response.httpHeaders()
                .headerNames()
                .forEach(headerName -> {
                    final String headerValue = response.httpHeaders().getHeaderValue(headerName);
                    responseByteBuilder
                            .append(headerName).append(": ").append(headerValue).append(SPACE)
                            .append(ENTER);
                });
    }

    private static void appendResponseBody(final HttpResponse response, final StringBuilder responseByteBuilder) {
        responseByteBuilder
                .append(ENTER)
                .append(response.responseBody().source());
    }
}
