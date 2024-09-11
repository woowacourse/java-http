package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestBody {

    private static final String EMPTY_BODY = "";
    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int LIMIT_SPLIT = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> body;

    public RequestBody(String body) {
        this.body = parseBody(body);
    }

    public static RequestBody empty() {
        return new RequestBody(EMPTY_BODY);
    }

    private Map<String, String> parseBody(String query) {
        List<String> pairs = List.of(query.split(PARAMETER_DELIMITER));
        return pairs.stream()
                .map(pair -> pair.split(KEY_VALUE_DELIMITER, LIMIT_SPLIT))
                .collect(Collectors.toMap(splitStrings -> splitStrings[KEY_INDEX],
                        splitStrings -> splitStrings[VALUE_INDEX])
                );
    }

    public boolean isEmpty() {
        return body.isEmpty();
    }

    public Map<String, String> getBody() {
        return body;
    }
}
