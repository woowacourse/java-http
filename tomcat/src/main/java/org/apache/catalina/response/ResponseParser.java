package org.apache.catalina.response;

import org.apache.catalina.ResourceResolver;

import java.util.Map;

public class ResponseParser {

    private static final String HEADER_FORMAT = "%s: %s \r\n";

    private final ResourceResolver resourceResolver;

    public ResponseParser() {
        this.resourceResolver = new ResourceResolver();
    }

    public String parse(HttpResponse response) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("HTTP/1.1 %d %s ", response.getStatus().code(), response.getStatus())).append("\r\n");

        Map<Header, Object> headers = response.getHeaders();
        for (Header header : headers.keySet()) {
            stringBuilder.append(String.format(HEADER_FORMAT, header.value(), headers.get(header)));
        }

        String responseBody = resourceResolver.resolve(response.getResponseBodyURI());
        stringBuilder.append("Content-Length: " + responseBody.getBytes().length + " \r\n")
                .append("\r\n")
                .append(responseBody);

        return stringBuilder.toString();
    }
}
