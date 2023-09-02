package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Collectors;

public class HttpResponseMessageWriter {

    public static void writeHttpResponse(final HttpResponse httpResponse, final OutputStream outputStream)
            throws IOException {

        final HttpResponseStatusLine httpResponseStatusLine = httpResponse.getHttpResponseStatusLine();
        final HttpResponseHeaders httpResponseHeaders = httpResponse.getHttpResponseHeaders();
        final String responseBody = httpResponse.getBody();

        final String responseMessage = String.join("\r\n",
                parseResponseStatusLine(httpResponseStatusLine),
                parseResponseHeaders(httpResponseHeaders),
                "",
                responseBody);

        outputStream.write(responseMessage.getBytes());
        outputStream.flush();
    }

    private static String parseResponseStatusLine(final HttpResponseStatusLine httpResponseStatusLine) {
        final HttpResponseStatus httpResponseStatus = httpResponseStatusLine.getHttpResponseStatus();
        return String.join(" ",
                httpResponseStatusLine.getHttpVersion(),
                String.valueOf(httpResponseStatus.getStatusCode()),
                httpResponseStatus.toString()
        );
    }

    private static String parseResponseHeaders(final HttpResponseHeaders httpResponseHeaders) {
        return httpResponseHeaders.getHeaders().entrySet().stream()
                .map(entry -> String.join(": ", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\r\n"));
    }
}
