package org.apache.coyote.http11.response;

import java.util.stream.Collectors;

import org.apache.coyote.http11.common.Headers;

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
        String responseHeaderView = statusLineToView() + generateHeaderView();
        String responseBodyView = responseBody.getValue();
        return responseHeaderView + ENTER + ENTER + responseBodyView;
    }

    private String statusLineToView() {
        return String.format(
                "%s %d %s %s",
                statusLine.getProtocolVersion(),
                statusLine.getHttpStatus().getStatusCode(),
                statusLine.getHttpStatus().getMessage(),
                ENTER
        );
    }

    private String generateHeaderView() {
        return headers.getValues().entrySet()
                .stream()
                .map(header -> header.getKey() + KEY_VALUE_DELIMITER + header.getValue() + BLANK)
                .collect(Collectors.joining(ENTER));
    }

}
