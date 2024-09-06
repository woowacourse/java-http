package org.apache.coyote.http11.domain.header;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestHeader {

    private static final String CONTENT_LENGTH = "Content-Length";
    public static final String HEADER_SPLITTER = ":";

    private final Map<String, String> headerValue;

    public RequestHeader(List<String> inputHeader) {
        this.headerValue = initHeader(inputHeader);
    }

    private Map<String, String> initHeader(List<String> inputHeader) {
        return inputHeader.stream()
                .map(header -> header.split(HEADER_SPLITTER))
                .collect(Collectors.toMap(
                        keyValue -> keyValue[0].trim(),
                        keyValue -> keyValue[1].trim()
                ));
    }

    public int getBodyLength() {
        if (headerValue.containsKey(CONTENT_LENGTH)) {
            return Integer.parseInt(headerValue.get(CONTENT_LENGTH));
        }
        return 0;
    }
}
