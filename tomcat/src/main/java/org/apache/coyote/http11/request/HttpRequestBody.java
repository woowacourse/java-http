package org.apache.coyote.http11.request;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HttpRequestBody {

    public static final String ENTRY_DELIMITER = "&";
    public static final String KEY_VALUE_DELIMITER = "=";
    public static final int KEY_INDEX = 0;
    public static final int VALUE_INDEX = 1;
    private final String body;

    public HttpRequestBody(final String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> parse() {
        return Pattern.compile(ENTRY_DELIMITER)
                .splitAsStream(body.trim())
                .map(entry -> entry.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.toUnmodifiableMap(entry -> entry[KEY_INDEX], entry -> entry[VALUE_INDEX]));
    }
}
