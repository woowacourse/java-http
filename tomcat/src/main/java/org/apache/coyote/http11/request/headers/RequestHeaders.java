package org.apache.coyote.http11.request.headers;

import org.apache.coyote.http11.common.header.HeaderProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

import static org.apache.coyote.http11.common.header.HeaderProperty.CONTENT_LENGTH;

public class RequestHeaders {

    private static final String NEW_LINE = "\r\n";
    private static final String HEADER_VALUE_SEPARATOR = ": ";
    private static final int HEADER_NAME_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;

    private final Map<String, String> requestHeader;

    private RequestHeaders(final Map<String, String> requestHeader) {
        this.requestHeader = requestHeader;
    }

    public static RequestHeaders from(final String header) {
        final Map<String, String> requestHeader = new HashMap<>();

        final StringTokenizer stringTokenizer = new StringTokenizer(header, NEW_LINE);
        while (stringTokenizer.hasMoreTokens()) {
            final String headerLine = stringTokenizer.nextToken();
            final String[] split = headerLine.split(HEADER_VALUE_SEPARATOR);
            requestHeader.put(split[HEADER_NAME_INDEX].trim(), split[HEADER_VALUE_INDEX].trim());
        }

        return new RequestHeaders(requestHeader);
    }

    public boolean isContentLengthNull() {
        return !requestHeader.containsKey(CONTENT_LENGTH.getPropertyName());
    }

    public int getContentLength() {
        if (isContentLengthNull()) {
            return 0;
        }

        return Integer.parseInt(requestHeader.get(CONTENT_LENGTH.getPropertyName()));
    }

    public String search(final HeaderProperty property) {
        return requestHeader.get(property.getPropertyName());
    }

    public boolean containsKey(final HeaderProperty property) {
        return requestHeader.containsKey(property.getPropertyName());
    }

    public Map<String, String> getRequestHeader() {
        return requestHeader;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RequestHeaders that = (RequestHeaders) o;
        return Objects.equals(requestHeader, that.requestHeader);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestHeader);
    }

    @Override
    public String toString() {
        return "RequestHeader{" +
                "requestHeader=" + requestHeader +
                '}';
    }
}
