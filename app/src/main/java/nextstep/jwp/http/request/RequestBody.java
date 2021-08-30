package nextstep.jwp.http.request;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestBody {

    private final String body;
    private final Map<String, String> query;

    public RequestBody(String body) {
        this(body, new ConcurrentHashMap<>());
        setQuery();
    }

    public RequestBody(String body, Map<String, String> query) {
        this.body = body;
        this.query = query;
    }

    public void setQuery() {
        String[] splitBody = body.split("&");

        for (String each : splitBody) {
            String[] splitEach = each.split("=");
            query.put(splitEach[0], splitEach[1]);
        }
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getQuery() {
        return query;
    }
}
