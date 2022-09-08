package org.apache.coyote.http11.httpmessage.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RequestBody {

    private final String body;

    public RequestBody(String body) {
        this.body = body;
    }

    public Map<String, Object> getParameters() {
        String[] params = body.split("&");

        Map<String, Object> parameters = new HashMap<>();
        for (String param : params) {
            put(parameters, param);
        }
        return parameters;
    }

    private void put(Map<String, Object> parameters, String param) {
        int index = param.indexOf("=");
        if (index != -1) {
            String key = param.substring(0, index);
            String value = param.substring(index + 1);

            parameters.put(key, value);
        }
    }

    public String getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestBody that = (RequestBody) o;
        return Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }

    @Override
    public String toString() {
        return body;
    }
}
