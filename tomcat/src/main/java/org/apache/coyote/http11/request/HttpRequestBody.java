package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestBody {
    private static final String BODY_DELIMITER = "&";
    private static final String PAIR_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> body;

    private HttpRequestBody(final Map<String, String> body) {
        this.body = body;
    }

    public static HttpRequestBody from(final String body) {
        return new HttpRequestBody(parseBody(body));
    }

    public static HttpRequestBody empty() {
        return new HttpRequestBody(Collections.emptyMap());
    }

    public static Map<String, String> parseBody(final String body) {
        return Arrays.stream(body.split(BODY_DELIMITER))
                .map(data -> data.split(PAIR_DELIMITER))
                .collect(Collectors.toMap(
                        data -> data[KEY_INDEX],
                        data -> data[VALUE_INDEX])
                );
    }

    public Map<String, String> getBody() {
        return this.body;
    }
}
