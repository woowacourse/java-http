package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.common.StaticResource;

public class ResponseHeader {

    private final Map<String, String> values;

    public ResponseHeader() {
        this(new LinkedHashMap<>());
    }

    public ResponseHeader(final Map<String, String> values) {
        this.values = values;
    }

    public void add(final String key, final String value) {
        this.values.put(key, value);
    }

    public void setContentInfo(final StaticResource staticResource) {
        add("Content-Type", staticResource.getContentType());
        add("Content-Length", staticResource.getContentLength());
    }

    public  void setLocation(final String location) {
        add("Location", location);
    }

    @Override
    public String toString() {
        return values.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.joining("\r\n"));
    }
}
