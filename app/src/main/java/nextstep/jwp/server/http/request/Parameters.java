package nextstep.jwp.server.http.request;

import java.util.HashMap;
import java.util.Map;

public class Parameters {
    private final Map<String, String> params;

    public Parameters() {
        this.params = new HashMap<>();
    }

    public void addParameters(String queryString) {
        if (queryString != null && !"".equals(queryString)) {
            String[] split = queryString.split("&");
            parse(split);
        }
    }

    private void parse(String[] split) {
        for (String data : split) {
            String[] splitData = data.split("=");
            params.put(splitData[0], splitData[1]);
        }
    }

    public String get(String key) {
        return params.get(key);
    }
}
