package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.constants.Query;

public class RequestBody {
    private final String body;
    private final Map<String, String> params;

    public RequestBody(String body) {
        this.body = body;
        this.params = new HashMap<>();
    }

    public Map<String, String> getParams() {
        String[] queries = body.split(Query.SEPARATOR);
        for (String query : queries) {
            String[] split = query.split(Query.EQUAL);
            this.params.put(split[Query.KEY], split[Query.VALUE]);
        }
        return this.params;
    }
}
