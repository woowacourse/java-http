package org.apache.coyote.request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestHeader {

    private final Map<String, String> headerValue;

    public RequestHeader(final List<String> headerLines) {
        this.headerValue = headerLines.stream()
                .map(headerLine -> headerLine.split(":"))
                .collect(Collectors.toMap(
                        keyValue -> keyValue[0],
                        keyValue -> keyValue[1]
                ));
    }
}
