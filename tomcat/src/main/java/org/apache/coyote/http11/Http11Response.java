package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Http11Response {

    private final String httpVersion;
    private final int httpStatusCode;
    private final String httpStatusMessage;
    private final Map<String, String> headers;
    private final String body;

    public Http11Response(
            final String httpVersion,
            final int httpStatusCode,
            final String httpStatusMessage,
            final Map<String, String> headers,
            final String body
    ) {
        this.httpVersion = httpVersion;
        this.httpStatusCode = httpStatusCode;
        this.httpStatusMessage = httpStatusMessage;
        this.headers = headers;
        this.body = body;
    }

    @Override
    public String toString() {
        final List<String> headerStrings = new ArrayList<>();
        for (final String key : headers.keySet()) {
            headerStrings.add(String.format("%s: %s ", key, headers.get(key)));
        }
        final String headerString = String.join("\r\n", headerStrings);
        return String.join("\r\n",
                String.format("%s %s %s ", httpVersion, httpStatusCode, httpStatusMessage),
                headerString,
                "",
                body
                );
    }
}
