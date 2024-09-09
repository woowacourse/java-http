package org.apache.coyote.http11;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestHeaders {

    final Map<String, String> headers;

    public RequestHeaders(List<String> rawRequestHeaders) {
        headers = rawRequestHeaders.stream()
                .map(rawRequestHeader -> rawRequestHeader.split(": "))
                .filter(requestHeaderPart -> requestHeaderPart.length == 2)
                .collect(Collectors.toMap(
                        requestHeaderPart -> requestHeaderPart[0],
                        requestHeaderPart -> requestHeaderPart[1])
                );
    }

    public String getHeaderValue(String name) {
        return headers.getOrDefault(name, "");
    }
}
