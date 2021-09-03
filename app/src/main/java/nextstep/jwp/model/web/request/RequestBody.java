package nextstep.jwp.model.web.request;

import java.util.Map;

public class RequestBody {

    private final Map<String, String> bodies;

    public RequestBody(Map<String, String> bodies) {
        this.bodies = bodies;
    }

    public String getValue(String key) {
        return bodies.get(key);
    }
}
