package nextstep.jwp.http;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestBody {
    private final Map<String, String> payload = new HashMap<>();

    public HttpRequestBody(final String payload) {
        if (payload.isEmpty()) {
            return;
        }
        Arrays.stream(payload.split("&"))
                .map(element -> element.split("="))
                .forEach(elementPair -> this.payload.put(elementPair[0], elementPair[1]));
    }

    public Map<String, String> getPayload() {
        return payload;
    }
}
