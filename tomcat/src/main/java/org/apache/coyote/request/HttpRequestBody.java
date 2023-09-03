package org.apache.coyote.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.common.ContentType;

public class HttpRequestBody {

    private static final String BODY_DELIMITER = "&";
    private static final String ELEMENT_DELIMITER = "=";

    private final String body;

    public HttpRequestBody() {
        this(null);
    }

    public HttpRequestBody(final String body) {
        this.body = body;
    }

    public Map<String, String> getBody(final ContentType contentType) {
        if (contentType == ContentType.APPLICATION_JSON) {
            return parseBodyForJson();
        }
        return new HashMap<>(Map.of("body", body));
    }

    private Map<String, String> parseBodyForJson() {
        return Arrays.stream(body.split(BODY_DELIMITER))
                .map(bodies -> bodies.split(ELEMENT_DELIMITER))
                .filter(bodies -> bodies.length == 2)
                .collect(Collectors.toMap(
                        bodies -> bodies[0],
                        bodies -> bodies[1],
                        (exist, replace) -> replace,
                        LinkedHashMap::new
                ));
    }
}
