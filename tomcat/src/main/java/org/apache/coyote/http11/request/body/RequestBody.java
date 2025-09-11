package org.apache.coyote.http11.request.body;

import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private final Map<String, String> parameters;

    public static RequestBody from(final String rawRequestBody) {
        final RequestBodyParser parser = RequestBodyParser.getInstance();
        final Map<String, String> bodyParameters = parser.parseBodyParameters(rawRequestBody);
        return new RequestBody(bodyParameters);
    }

    public static RequestBody createEmptyBody() {
        return new RequestBody(new HashMap<>());
    }

    public String getParameter(final String key) {
        return parameters.get(key);
    }

    private RequestBody(final Map<String, String> parameters) {
        this.parameters = new HashMap<>(parameters);
    }
}
