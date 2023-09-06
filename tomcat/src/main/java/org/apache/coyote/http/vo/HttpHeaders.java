package org.apache.coyote.http.vo;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.coyote.http.HttpHeader;
import util.MultiValueMap;

public class HttpHeaders {

    private static final String KEY_VALUE_DELIMITER = ":";
    private static final String VALUE_DELIMITER = ",";

    private final MultiValueMap<HttpHeader, String> headers;

    private HttpHeaders(final MultiValueMap<HttpHeader, String> headers) {
        this.headers = headers;
    }

    public void put(final HttpHeader key, final String value) {
        this.headers.put(key, value);
    }

    public void putAll(final HttpHeader key, final List<String> values) {
        this.headers.putAll(key, values);
    }

    public Optional<String> getRecentHeaderValue(final HttpHeader header) {
        return Optional.ofNullable(headers.getRecentValue(header));
    }

    public List<String> getHeaderValues(final HttpHeader header) {
        return headers.getValues(header);
    }

    public static HttpHeaders getEmptyHeaders() {
        return new HttpHeaders(new MultiValueMap<>());
    }

    public String getRawHeaders() {
        Map<HttpHeader, List<String>> multiValueMap = headers.getMultiValueMap();
        return multiValueMap.entrySet().stream()
                .map(HttpHeaders::getHeaderFormat)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private static String getHeaderFormat(final Entry<HttpHeader, List<String>> it) {
        return it.getKey().getKey() + KEY_VALUE_DELIMITER + " " + String.join(VALUE_DELIMITER, it.getValue());
    }
}
