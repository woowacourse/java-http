package org.apache.coyote.http11.response;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {

    private final HttpStatusCode statusCode;
    private final Map<String, String> responseHeader;
    private final String responseBody;

    public HttpResponse(HttpStatusCode statusCode, Map<String, String> responseHeader, String responseBody) {
        this.statusCode = statusCode;
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    public String getResponse() {
        final List<String> headers = responseHeader.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.toList());

        String result = "HTTP/1.1 " + statusCode.getCode() + " " + statusCode.getValue() + " ";

        for (String header : headers) {
            result = String.join("\r\n", result, header);
        }

        return String.join("\r\n",
                result,
                "",
                responseBody);
    }
}
