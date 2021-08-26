package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;

public class RequestBody {
    private final String body;
    private final Map<String, String> params;

    public RequestBody(String body) {
        this.body = body;
        this.params = new HashMap<>();
    }

    public Map<String, String> getParams() {
        String[] queries = body.split("&");
        for (String query : queries) {
            String[] split = query.split("=");
            this.params.put(split[0], split[1]);
        }
        return this.params;
    }
}
