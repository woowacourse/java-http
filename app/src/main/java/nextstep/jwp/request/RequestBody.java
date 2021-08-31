package nextstep.jwp.request;

import java.util.LinkedHashMap;
import java.util.Map;
import nextstep.jwp.constants.Query;

public class RequestBody {
    private final String body;

    public RequestBody(String body) {
        this.body = body;
    }

    public Map<String, String> getParams() {
        final Map<String, String> params = new LinkedHashMap<>();
        String[] queries = body.split(Query.SEPARATOR);
        for (String query : queries) {
            String[] split = query.split(Query.EQUAL);
            params.put(split[Query.KEY].trim(), split[Query.VALUE].trim());
        }
        return params;
    }
}
