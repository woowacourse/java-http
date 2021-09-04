package nextstep.jwp.request;

import java.util.LinkedHashMap;
import java.util.Map;
import nextstep.jwp.constants.HttpTerms;

public class RequestBody {
    private final String body;

    public RequestBody(String body) {
        this.body = body;
    }

    public Map<String, String> getParams() {
        final Map<String, String> params = new LinkedHashMap<>();
        String[] queries = body.split(HttpTerms.AND_PERCENT_SEPARATOR);
        for (String query : queries) {
            String[] split = query.split(HttpTerms.EQUAL_SEPARATOR);
            params.put(split[HttpTerms.KEY].trim(), split[HttpTerms.VALUE].trim());
        }
        return params;
    }
}
