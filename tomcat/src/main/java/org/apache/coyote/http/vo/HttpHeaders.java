package org.apache.coyote.http.vo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.coyote.http.HttpHeader;
import util.MultiValueMap;

public class HttpHeaders {

    private static final String KEY_VALUE_DELIMITER = ":";
    private static final String VALUE_DELIMITER = ",";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private final MultiValueMap<HttpHeader, String> headers;

    private HttpHeaders(final MultiValueMap<HttpHeader, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders of(final String rawHeaders) {
        return new HttpHeaders(parsingHeaders(rawHeaders));
    }

    private static MultiValueMap<HttpHeader, String> parsingHeaders(final String rawHeaders) {
        final MultiValueMap<HttpHeader, String> headers = new MultiValueMap<>();

        for (final String rawHeader : rawHeaders.split(System.lineSeparator())) {
            final String[] keyWithValue = rawHeader.split(KEY_VALUE_DELIMITER);
            final HttpHeader key = HttpHeader.of(keyWithValue[KEY_INDEX]);

            if (key != null) {
                headers.putAll(key,
                        Arrays.stream(keyWithValue[VALUE_INDEX].split(VALUE_DELIMITER))
                        .map(String::trim)
                        .collect(Collectors.toUnmodifiableList())
                );
            }
        }

        return headers;
    }

    public List<String> getHeaderValues(final HttpHeader header) {
        return headers.getValues(header);
    }

    public static HttpHeaders getEmptyHeaders(){
        return new HttpHeaders(new MultiValueMap<>());
    }
}
