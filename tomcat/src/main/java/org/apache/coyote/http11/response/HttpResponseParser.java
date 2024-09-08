package org.apache.coyote.http11.response;

import java.util.ArrayList;
import java.util.List;

public class HttpResponseParser {

    private static final String DELIMITER_LINE = "\r\n";

    private static final String DELIMITER_HEADER = ": ";

    public String parseResponse(HttpResponse httpResponse) {
        String responseLine = parseResponseLine(httpResponse.getHttpStatusCode());
        String responseHeader = parseResponseHeaders(httpResponse.getHttpResponseHeaders());
        String responseBody = httpResponse.getHttpResponseBody().body();
        return String.join(DELIMITER_LINE,
                responseLine,
                responseHeader,
                "",
                responseBody);
    }

    private String parseResponseLine(HttpStatusCode httpStatusCode) {
        int code = httpStatusCode.getCode();
        String message = httpStatusCode.getMessage();
        return "HTTP/1.1 " + code + " " + message;
    }

    private String parseResponseHeaders(HttpResponseHeaders httpResponseHeaders) {
        List<String> headers = new ArrayList<>();
        String[] keys = httpResponseHeaders.getKeys();
        for (String key : keys) {
            headers.add(key + DELIMITER_HEADER + httpResponseHeaders.getValue(key));
        }
        return String.join("\r\n", headers);
    }
}
