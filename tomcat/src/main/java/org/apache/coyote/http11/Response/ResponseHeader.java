package org.apache.coyote.http11.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.model.Headers;

public class ResponseHeader {

    private final Map<String, String> headers = new HashMap<>();

    public ResponseHeader() {
    }

    public String getResponse() {
        return headers.entrySet().stream()
                .map(it -> it.getKey() + ": " + it.getValue() + " ")
                .collect(Collectors.joining("\r\n"));
    }

    public void addHeader(final String name, final String value) {
        headers.put(name, value);
    }

    public void addContentType(final String contentType) {
        if (headers.containsKey(Headers.CONTENT_TYPE.getName())) {
            headers.put(Headers.CONTENT_TYPE.getName(),
                    headers.get(Headers.CONTENT_TYPE.getName()) + ";" + contentType);
            return;
        }
        headers.put(Headers.CONTENT_TYPE.getName(), contentType);
    }

    public void setCookie(final String cookie) {
        headers.put(Headers.SET_COOKIE.getName(), cookie);
    }

    public void setLocation(final String location) {
        headers.put(Headers.LOCATION.getName(), location);
    }
}
