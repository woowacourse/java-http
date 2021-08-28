package nextstep.jwp;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractController implements Controller {

    protected String resource;

    protected AbstractController(String resource) {
        this.resource = resource;
    }

    @Override
    public String getResource() {
        return resource;
    }

    protected Map<String, String> extractQuery(String queryString) {
        final Map<String, String> queryInfo = new HashMap<>();
        final String[] queries = queryString.split("&");
        for (String query : queries) {
            final int index = query.indexOf('=');
            final String key = query.substring(0, index);
            final String value = query.substring(index + 1);
            queryInfo.put(key, value);
        }
        return queryInfo;
    }
}
