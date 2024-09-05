package org.apache.coyote.request;

import com.techcourse.exception.UncheckedServletException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeader {

    private final Map<String, String> headers;

    public HttpHeader(List<String> rawHeaders) {
        this.headers = rawHeaders.stream()
                .peek(rawHeader -> {
                    if (!rawHeader.contains(": ")) {
                        throw new UncheckedServletException("형식이 올바르지 않은 헤더가 포함되어 있습니다.");
                    }
                })
                .collect(Collectors.toMap(
                        rawHeader -> rawHeader.substring(0, rawHeader.indexOf(": ")),
                        rawHeader -> rawHeader.substring(rawHeader.indexOf(": ") + 2)
                ));
    }

    public String get(String name) {
        return headers.get(name);
    }

    public boolean contains(String name) {
        return headers.containsKey(name);
    }
}
