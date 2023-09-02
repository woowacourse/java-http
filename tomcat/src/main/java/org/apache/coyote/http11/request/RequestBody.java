package org.apache.coyote.http11.request;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private static final String SEPARATOR = "&";
    private static final String DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> items = new HashMap<>();

    private RequestBody() {
    }

    private RequestBody(final Map<String, String> items) {
        this.items.putAll(items);
    }

    public static RequestBody empty() {
        return new RequestBody();
    }

    public static RequestBody from(final String body) {
        if (body.isEmpty()) {
            return new RequestBody();
        }
        return Arrays.stream(body.split(SEPARATOR))
                .map(field -> field.split(DELIMITER))
                .collect(collectingAndThen(
                        toMap(field -> field[KEY_INDEX], field -> field[VALUE_INDEX]),
                        RequestBody::new
                ));
    }

    public String get(final String key) {
        return items.get(key);
    }

    public Map<String, String> getItems() {
        return items;
    }
}
