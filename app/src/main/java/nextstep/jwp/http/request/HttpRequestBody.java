package nextstep.jwp.http.request;

import nextstep.jwp.util.QueryParamsParser;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestBody {
    private final Map<String, String> payload = new HashMap<>();

    public HttpRequestBody(final String requestBody) {
        if (requestBody.isEmpty()) {
            return;
        }

        QueryParamsParser.parse(requestBody, payload);
    }

    public Map<String, String> getPayload() {
        return payload;
    }
}
