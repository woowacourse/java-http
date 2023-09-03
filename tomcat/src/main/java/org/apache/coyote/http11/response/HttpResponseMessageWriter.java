package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Collectors;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.HttpStatus;

public class HttpResponseMessageWriter {

    private HttpResponseMessageWriter() {
    }

    public static void writeHttpResponse(final HttpResponse httpResponse, final OutputStream outputStream)
            throws IOException {
        final HttpResponseStatusLine httpResponseStatusLine = httpResponse.getHttpResponseStatusLine();
        final HttpHeaders httpHeaders = httpResponse.getHttpResponseHeaders();
        final String responseBody = httpResponse.getBody();
        final String responseMessage = String.join("\r\n",
                parseResponseStatusLine(httpResponseStatusLine),
                parseResponseHeaders(httpHeaders),
                "",
                responseBody);

        outputStream.write(responseMessage.getBytes());
        outputStream.flush();
    }

    private static String parseResponseStatusLine(final HttpResponseStatusLine httpResponseStatusLine) {
        final HttpStatus httpStatus = httpResponseStatusLine.getHttpResponseStatus();
        return String.join(" ",
                httpResponseStatusLine.getHttpVersion(),
                String.valueOf(httpStatus.getStatusCode()),
                httpStatus.toString()
        ) + " ";
    }

    private static String parseResponseHeaders(final HttpHeaders httpHeaders) {
        return httpHeaders.getHeaders().entrySet().stream()
                .map(entry -> String.join(": ", entry.getKey(), entry.getValue() + " "))
                .collect(Collectors.joining("\r\n"));
    }
}
