package nextstep.jwp.model;

import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private final String body;

    public RequestBody(String body) {
        this.body = body;
    }

    public Map<String, String> queries() {
        String[] querySlices = body.split("&");
        Map<String, String> queries = new HashMap<>();
        for (String querySlice : querySlices) {
            String[] query = querySlice.split("=");
            queries.put(query[0], query[1]);
        }
        return queries;
    }
}
