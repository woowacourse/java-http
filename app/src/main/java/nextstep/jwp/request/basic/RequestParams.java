package nextstep.jwp.request.basic;

import java.util.HashMap;
import java.util.Map;

public class RequestParams {

    private String body;
    private Map<String, String> params;

    public RequestParams() {
        this.params = new HashMap<>();
    }

    public void addBody(String content) {
        this.body = content;
    }

    public void addParams(String key, String value) {
        this.params.put(key, value);
    }

    public void addQueryString(String queryString) {

    }
}
