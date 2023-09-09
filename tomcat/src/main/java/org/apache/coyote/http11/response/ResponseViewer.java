package org.apache.coyote.http11.response;

import java.util.stream.Collectors;

import org.apache.coyote.http11.Headers;

public class ResponseViewer {
    private static final String ENTER = "\r\n";
    private static final String BLANK = " ";
    private static final String KEY_VALUE_DELIMITER = ": ";

    private final StatusLine statusLine;
    private final Headers headers;
    private final ResponseBody responseBody;

    private ResponseViewer(StatusLine statusLine, Headers headers, ResponseBody responseBody) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public static ResponseViewer from(HttpResponse response) {
        return new ResponseViewer(
                response.getStatusLine(),
                response.getHeaders(),
                response.getResponseBody()
        );
    }

    public String getView() {
        String protocolVersion = statusLine.getProtocolVersion();
        int statusCode = statusLine.getHttpStatus().getStatusCode();
        String message = statusLine.getHttpStatus().getMessage();

        String statusLineResponse = protocolVersion + BLANK + statusCode + BLANK + message + BLANK + ENTER;

        String headerResponse = headers.getValues().entrySet()
                .stream()
                .map(header -> header.getKey() + KEY_VALUE_DELIMITER + header.getValue() + BLANK)
                .collect(Collectors.joining(ENTER));
        return statusLineResponse + headerResponse + ENTER + ENTER + responseBody.getValue();
    }

}
