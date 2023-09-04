package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpResponseWriter {

    private HttpResponseWriter() {
    }

    public static void write(final OutputStream outputStream, final HttpResponse httpResponse) throws IOException {
        final String response = String.join("\r\n",
                extractStartLine(httpResponse),
                extractHeaders(httpResponse),
                "",
                httpResponse.getBody());

        outputStream.write(response.getBytes());
    }

    private static String extractStartLine(final HttpResponse httpResponse) {
        final String httpVersion = httpResponse.getHttpVersion();
        final int statusCode = httpResponse.getStatusCode().getCode();
        final String statusText = httpResponse.getStatusCode().getText();
        return String.join(" ", httpVersion, String.valueOf(statusCode), statusText) + " ";
    }

    private static String extractHeaders(final HttpResponse httpResponse) {
        final Map<HttpResponseHeader, String> headers = httpResponse.getHeaders();
        final List<String> headerLines = new ArrayList<>();
        for (Entry<HttpResponseHeader, String> entry : headers.entrySet()) {
            final String headerLine = entry.getKey().getHeader()
                    + ": "
                    + entry.getValue()
                    + " ";
            headerLines.add(headerLine);
        }
        return String.join("\r\n", headerLines);
    }
}
