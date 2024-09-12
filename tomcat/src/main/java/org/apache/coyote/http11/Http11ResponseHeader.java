package org.apache.coyote.http11;

import java.net.http.HttpHeaders;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Http11ResponseHeader {

    private static final String KEY_VALUE_DELIMITER = ": ";
    private static final String NEW_LINE = "\r\n";
    private final HttpHeaders httpHeaders;

    private Http11ResponseHeader(HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public static Http11ResponseHeader of() {
        return new Http11ResponseHeader(HttpHeaders.of(Map.of(), (s1, s2) -> true));
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getAllHeaders() {
        StringBuilder headerString = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : httpHeaders.map().entrySet()) {
            String key = entry.getKey();
            for (String value : entry.getValue()) {
                headerString.append(key).append(KEY_VALUE_DELIMITER).append(value).append(NEW_LINE);
            }
        }
        return headerString.toString();
    }

    public String getHeader(String name) {
        List<String> values = httpHeaders.allValues(name);
        return String.join(",", values);
    }

    public static class Builder {
        private final Map<String, List<String>> headers = new HashMap<>();

        public Builder addHeader(String name, List<String> values) {
            headers.put(name, values);
            return this;
        }

        public Http11ResponseHeader build() {
            return new Http11ResponseHeader(HttpHeaders.of(headers, (s1, s2) -> true));
        }
    }

    @Override
    public String toString() {
        return "Http11ResponseHeader{" +
                "httpHeaders=" + httpHeaders +
                '}';
    }
}
