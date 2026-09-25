package org.apache.coyote.http.request;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class FormBody implements RequestBody {

    private final Map<String, String> parameters;

    private FormBody(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public static FormBody from(String rawBody) {
        Map<String, String> parameters = Arrays.stream(rawBody.split("&"))
                .map(pair -> pair.split("=", 2))
                .filter(pair -> pair.length == 2)
                .collect(Collectors.toMap(
                        pair -> decode(pair[0]),
                        pair -> decode(pair[1]),
                        (first, second) -> first));
        return new FormBody(Collections.unmodifiableMap(parameters));
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(parameters.get(key));
    }
}
