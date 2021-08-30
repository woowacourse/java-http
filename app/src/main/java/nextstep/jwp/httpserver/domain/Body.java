package nextstep.jwp.httpserver.domain;

import java.util.HashMap;
import java.util.Map;

public class Body {
    private final Map<String, String> body;

    public Body() {
        body = new HashMap<>();
    }

    public Body(Map<String, String> body) {
        this.body = body;
    }

    public Map<String, String> getBody() {
        return body;
    }
}
