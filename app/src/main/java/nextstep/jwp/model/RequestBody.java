package nextstep.jwp.model;

import java.util.Map;

public class RequestBody {

    private Map<String, String> requestBodies;

    private RequestBody(Map<String, String> requestBodies) {
        this.requestBodies = requestBodies;
    }

    public static RequestBody of(Map<String, String> requestBodies) {
        return new RequestBody(requestBodies);
    }

    public String getParameter(String key) {
        return requestBodies.get(key);
    }
}
