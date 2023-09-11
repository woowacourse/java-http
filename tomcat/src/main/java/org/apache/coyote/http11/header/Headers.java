package org.apache.coyote.http11.header;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Headers {

    private final Map<Header, String> value = new LinkedHashMap<>();

    public void addHeader(final Header header,
                          final String value) {
        this.value.put(header, value);
    }

    public String parseResponse() {
        final List<String> stringHeaders = new ArrayList<>();
        for (Header header : value.keySet()) {
            final String format = String.format("%s: %s ", header.getValue(), value.get(header));
            stringHeaders.add(format);
        }
        return String.join("\r\n", stringHeaders);
    }

    public void addRequestHeaders(final List<String> requestHeaderLines) {
        for (int i = 1; i < requestHeaderLines.size(); i++) {
            final String requestHeader = requestHeaderLines.get(i);
            final String[] requestHeaderNameAndValue = requestHeader.split(":");
            final String requestHeaderName = requestHeaderNameAndValue[0].trim().toLowerCase();
            final String requestHeaderValue = requestHeaderNameAndValue[1].trim().toLowerCase();
            addRequestHeader(requestHeaderName, requestHeaderValue);
        }
    }

    private void addRequestHeader(final String requestHeaderName,
                                  final String requestHeaderValue) {
        try {
            value.put(RequestHeader.from(requestHeaderName), requestHeaderValue);
        } catch (RuntimeException e) {
            addEntityHeader(requestHeaderName, requestHeaderValue);
        }
    }

    private void addEntityHeader(final String requestHeaderName,
                                 final String requestHeaderValue) {
        try {
            value.put(EntityHeader.from(requestHeaderName), requestHeaderValue);
        } catch (RuntimeException ignored) {

        }
    }

    public String getValue(final Header header) {
        return value.getOrDefault(header, "");
    }

    @Override
    public String toString() {
        return "Headers{" +
                "headers=" + value +
                '}';
    }
}
