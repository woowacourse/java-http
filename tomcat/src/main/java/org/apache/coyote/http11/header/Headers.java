package org.apache.coyote.http11.header;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Headers {

    private final Map<Header, String> values = new LinkedHashMap<>();

    public void addHeader(final Header header,
                          final String value) {
        this.values.put(header, value);
    }

    public String parseResponse() {
        final List<String> stringHeaders = new ArrayList<>();
        for (Map.Entry<Header, String> headerStringEntry : values.entrySet()) {
            final Header key = headerStringEntry.getKey();
            final String value = headerStringEntry.getValue();
            final String format = String.format("%s: %s ", key.getValue(), value);
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
            values.put(RequestHeader.from(requestHeaderName), requestHeaderValue);
        } catch (RuntimeException e) {
            addEntityHeader(requestHeaderName, requestHeaderValue);
        }
    }

    private void addEntityHeader(final String requestHeaderName,
                                 final String requestHeaderValue) {
        try {
            values.put(EntityHeader.from(requestHeaderName), requestHeaderValue);
        } catch (RuntimeException ignored) {

        }
    }

    public String getValue(final Header header) {
        return values.getOrDefault(header, "");
    }

    @Override
    public String toString() {
        return "Headers{" +
                "headers=" + values +
                '}';
    }
}
