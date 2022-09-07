package org.apache.coyote.http11.request;

import java.util.Map;
import org.apache.support.ParameterBinder;

public class RequestBody {

    private final Map<String, String> values;

    private RequestBody(Map<String, String> values) {
        this.values = values;
    }

    public static RequestBody from(String requestBody) {
        return new RequestBody(ParameterBinder.bind(requestBody));
    }

    public Map<String, String> getValues() {
        return values;
    }
}
