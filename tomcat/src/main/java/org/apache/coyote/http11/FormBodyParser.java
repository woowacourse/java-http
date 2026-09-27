package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

final class FormBodyParser {

    private final String body;
    private Map<String, String> parameters;

    FormBodyParser(String body) {
        this.body = body;
    }

    String getParameter(String name) {
        if (parameters == null) {
            parameters = parse();
        }
        return parameters.get(name);
    }

    private Map<String, String> parse() {
        Map<String, String> parsed = new HashMap<>();
        if (body.isBlank()) {
            return parsed;
        }

        for (String pair : body.split("&")) {
            String[] nameAndValue = pair.split("=", 2);
            String value = nameAndValue.length == 2 ? nameAndValue[1] : "";
            String name = URLDecoder.decode(nameAndValue[0], StandardCharsets.UTF_8);
            parsed.put(name, URLDecoder.decode(value, StandardCharsets.UTF_8));
        }
        return parsed;
    }
}
