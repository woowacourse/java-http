package org.apache.coyote.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestBody {

    private final Map<String, String> values;

    private RequestBody(final Map<String, String> values) {
        this.values = values;
    }

    public static RequestBody parse(final String input) {
        if (input.isBlank()) {
            return new RequestBody(new HashMap<>());
        }

        final List<String> messages = List.of(input.split("&"));
        final Map<String, String> values = new HashMap<>();

        for (String message : messages) {
            final List<String> value = List.of(message.split("="));
            values.put(value.get(0), value.get(1));
        }

        return new RequestBody(values);
    }

    public Map<String, String> getValues() {
        return values;
    }
}
