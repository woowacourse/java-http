package nextstep.jwp.http;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static nextstep.jwp.http.HttpRequest.*;

public class HttpRequestBody {
    private final Map<String, String> payload = new HashMap<>();

    public HttpRequestBody(final String payload) {
        if (payload.isEmpty()) {
            return;
        }

        Arrays.stream(payload.split(QUERY_STRING_DELIMITER))
                .map(element -> element.split(QUERY_KEY_VALUE_DELIMITER))
                .forEach(elementPair -> this.payload.put(elementPair[KEY_INDEX], elementPair[VALUE_INDEX]));
    }

    public Map<String, String> getPayload() {
        return payload;
    }
}
