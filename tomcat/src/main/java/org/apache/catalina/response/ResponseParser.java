package org.apache.catalina.response;

import java.util.Map;
import java.util.StringJoiner;

public class ResponseParser {

    private static final String STATUS_LINE_FORMAT = "%s %d %s";
    private static final String HEADER_FORMAT = "%s: %s";
    private static final String LINE_SEPARATOR = "\r\n";

    public String parse(HttpResponse response) {
        StringJoiner responseJoiner = new StringJoiner(" " + LINE_SEPARATOR);
        StatusLine statusLine = response.getStatusLine();
        responseJoiner.add(String.format(STATUS_LINE_FORMAT,
                statusLine.getProtocolVersion(),
                statusLine.getStatusCode(),
                statusLine.getStatusMessage()));

        Map<Header, Object> headers = response.getHeaders();
        for (Header header : headers.keySet()) {
            responseJoiner.add(String.format(HEADER_FORMAT,
                    header.value(),
                    headers.get(header)));
        }

        String body = response.getBody();
        responseJoiner.add(String.format(HEADER_FORMAT,
                        Header.CONTENT_LENGTH.value(),
                        body.getBytes().length))
                .add(LINE_SEPARATOR + body);

        return responseJoiner.toString();
    }
}
