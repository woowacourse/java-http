package nextstep.jwp.http.reqeust;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestBody {

    private static final String VALUE_CONNECTOR = "&";
    private static final String VALUE_SEPARATOR = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private Map<String, String> requestBodies;

    public HttpRequestBody(final String requestBody) {
        Map<String, String> requestBodies = new HashMap<>();

        if (!requestBody.isBlank()) {
            insertValue(requestBodies, requestBody);
        }
        this.requestBodies = requestBodies;
    }

    private void insertValue(final Map<String, String> requestBodies, final String requestBody) {
        String[] bodies = requestBody.split(VALUE_CONNECTOR);
        for (String body : bodies) {
            String[] value = body.split(VALUE_SEPARATOR);
            requestBodies.put(value[KEY_INDEX], value[VALUE_INDEX]);
        }
    }

    public Map<String, String> getRequestBodies() {
        return requestBodies;
    }
}
