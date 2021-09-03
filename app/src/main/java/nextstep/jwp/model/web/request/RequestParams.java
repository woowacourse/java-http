package nextstep.jwp.model.web.request;

import java.util.HashMap;
import java.util.Map;

public class RequestParams {

    private final Map<String, String> params = new HashMap<>();

    public void add(String key, String value) {
        params.put(key, value);
    }
}
