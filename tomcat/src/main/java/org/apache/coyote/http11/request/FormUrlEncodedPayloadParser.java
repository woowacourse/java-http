package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class FormUrlEncodedPayloadParser implements PayloadParser {

    private static final String PAYLOAD_SEPARATOR = "&";
    private static final String PAYLOAD_DELIMITER = "=";

    @Override
    public Map<String, String> parse(final String body) {
        return Arrays.stream(body.split(PAYLOAD_SEPARATOR))
                .map(param -> param.split(PAYLOAD_DELIMITER))
                .collect(Collectors.toMap(e -> e[0], e -> e[1]));
    }
}
