package org.apache.catalina.response;

import java.util.Map;

public class ResponseParser {

    private static final String HEADER_FORMAT = "%s: %s \r\n";

    public String parse(HttpResponse response) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("HTTP/1.1 %d %s ", response.getStatus().code(), response.getStatus())).append("\r\n");

        Map<Header, Object> headers = response.getHeaders();
        for (Header header : headers.keySet()) {
            stringBuilder.append(String.format(HEADER_FORMAT, header.value(), headers.get(header)));
        }

        String body = response.getBody();
        stringBuilder.append(Header.CONTENT_LENGTH.value() + body.getBytes().length + " \r\n")
                .append("\r\n")
                .append(body);

        return stringBuilder.toString();
    }
}
