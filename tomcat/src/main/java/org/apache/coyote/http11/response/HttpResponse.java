package org.apache.coyote.http11.response;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {

    private static final String PROTOCOL_VERSION = "HTTP/1.1";
    private static final String INDENT = " ";

    private final HttpStatusCode statusCode;
    private final Map<String, String> responseHeader;
    private final String responseBody;

    public HttpResponse(HttpStatusCode statusCode, Map<String, String> responseHeader, String responseBody) {
        this.statusCode = statusCode;
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    public String getResponse() {
        final String startLine =
                PROTOCOL_VERSION + INDENT + statusCode.getCode() + INDENT + statusCode.getValue() + INDENT;

        return String.join("\r\n",
                startLine,
                convertHeaderToResponseText(),
                "",
                responseBody);
    }

    private String convertHeaderToResponseText() {
        final List<String> headers = responseHeader.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + INDENT)
                .collect(Collectors.toList());

        return String.join("\r\n", headers);
    }
}
