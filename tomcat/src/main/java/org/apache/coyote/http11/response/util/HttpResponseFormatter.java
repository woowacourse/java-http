package org.apache.coyote.http11.response.util;

import java.util.Map.Entry;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpProtocolVersion;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class HttpResponseFormatter {

    private static final HttpResponseFormatter instance = new HttpResponseFormatter();

    private HttpResponseFormatter() {
    }

    public String format(final HttpResponse response) {
        final String statusLine = formatStatusLine(response.protocolVersion(), response.status());
        final String headers = formatHeaders(response.headers());

        if (response.body() == null || response.body().isEmpty()) {
            return String.join(
                    "\r\n",
                    statusLine, headers
            );
        }

        return String.join(
                "\r\n",
                statusLine, headers, response.body()
        );
    }

    private String formatStatusLine(
            final HttpProtocolVersion protocolVersion,
            final HttpStatus status
    ) {
        return protocolVersion.getVersion() + " "
               + status.getCode() + " "
               + status.getMessage() + " ";
    }

    public String formatHeaders(final HttpHeaders headers) {
        final StringBuilder sb = new StringBuilder();
        for (final Entry<String, String> entry : headers.getHeaders().entrySet()) {
            sb.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append("\r\n");
        }

        return sb.toString();
    }

    public static HttpResponseFormatter getInstance() {
        return instance;
    }
}
