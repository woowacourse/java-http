package org.apache.coyote.http11.request.bodyParser;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FormBodyParser implements BodyParser {

    @Override
    public boolean supports(String contentType) {
        return contentType.startsWith(
                "application/x-www-form-urlencoded"
        );
    }

    @Override
    public Map<String, String> parse(String body) {
        Map<String, String> parameters = new HashMap<>();

        if (body == null || body.isBlank()) {
            return parameters;
        }

        for (String parameter : body.split("&")) {
            String[] keyValue = parameter.split("=", 2);

            if (keyValue.length == 2) {
                parameters.put(
                        decode(keyValue[0]),
                        decode(keyValue[1])
                );
            }
        }

        return parameters;
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
