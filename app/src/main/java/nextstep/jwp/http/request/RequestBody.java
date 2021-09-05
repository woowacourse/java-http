package nextstep.jwp.http.request;

import java.util.Map;

public class RequestBody {

    private final Map<String, String> requestBodies;

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
