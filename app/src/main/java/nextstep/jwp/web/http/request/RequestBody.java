package nextstep.jwp.web.http.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private Map<String, String> attributes = new HashMap<>();

    private RequestBody() {
    }

    public RequestBody(String body) {
        if (body == null) {
            attributes = null;
            return;
        }

        parseBodyToAttributes(body);
    }

    private void parseBodyToAttributes(String body) {
        String[] parsedValue = body.split("&");
        Arrays.stream(parsedValue)
            .forEach(value -> {
                String[] keyAndValue = value.split("=");
                attributes.put(keyAndValue[0], keyAndValue[1]);
            });
    }

    public String getAttribute(String key) {
        return attributes.getOrDefault(key, null);
    }
}
