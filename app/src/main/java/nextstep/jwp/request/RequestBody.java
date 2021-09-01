package nextstep.jwp.request;

import java.util.LinkedHashMap;
import java.util.Map;
import nextstep.jwp.constants.Http;

public class RequestBody {
    private final String body;

    public RequestBody(String body) {
        this.body = body;
    }

    public Map<String, String> getParams() {
        final Map<String, String> params = new LinkedHashMap<>();
        String[] queries = body.split(Http.AND_PERCENT_SEPARATOR);
        for (String query : queries) {
            String[] split = query.split(Http.EQUAL_SEPARATOR);
            params.put(split[Http.KEY].trim(), split[Http.VALUE].trim());
        }
        return params;
    }
}
