package org.apache.coyote.http11.header;

import org.apache.coyote.http11.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Headers {

    private static final Logger log = LoggerFactory.getLogger(Request.class);

    private final Map<Header, String> headers = new LinkedHashMap<>();

    public void addHeader(final Header header,
                            final String value) {
        headers.put(header, value);
    }

    public String parseResponse() {
        final List<String> stringHeaders = new ArrayList<>();
        for (Header header : headers.keySet()) {
            final String format = String.format("%s: %s ", header.getValue(), headers.get(header));
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
            headers.put(RequestHeader.from(requestHeaderName), requestHeaderValue);
        } catch (RuntimeException e) {
            addEntityHeader(requestHeaderName, requestHeaderValue);
        }
    }

    private void addEntityHeader(final String requestHeaderName,
                                 final String requestHeaderValue) {
        try {
            headers.put(EntityHeader.from(requestHeaderName), requestHeaderValue);
        } catch (RuntimeException ignored) {

        }
    }

    public String getValue(final Header header) {
        return headers.getOrDefault(header, "");
    }
}
