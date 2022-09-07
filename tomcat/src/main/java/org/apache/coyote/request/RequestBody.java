package org.apache.coyote.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.exception.InvalidRequestException;

public class RequestBody {

    private static final String BODY_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> value;

    private RequestBody(final Map<String, String> value) {
        this.value = value;
    }

    public static RequestBody from(final String requestBody) {
        if (requestBody.isBlank() || requestBody == null) {
            return new RequestBody(new HashMap<>());
        }
        Map<String, String> body = new HashMap<>();
        String[] tokens = requestBody.split(BODY_SEPARATOR);
        for (String token : tokens) {
            String[] entry = token.split(KEY_VALUE_SEPARATOR);
            body.put(entry[KEY_INDEX], entry[VALUE_INDEX]);
        }
        return new RequestBody(body);
    }

    public String getValue(final String name) {
        return Optional.ofNullable(value.get(name))
                .orElseThrow(InvalidRequestException::new);
    }
}
