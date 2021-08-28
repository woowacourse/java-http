package nextstep.jwp;

import java.util.HashMap;
import java.util.Map;

public class Parameters {
    private Map<String, String> parameters;

    public Parameters(String queryString, String requestBody) {
        this.parameters = new HashMap<>();
        parseParameters(queryString, requestBody);
    }

    private void parseParameters(String queryString, String requestBody) {
        parseQueryString(queryString);
        parseQueryString(requestBody);
    }

    private void parseQueryString(String queryString) {
        if (queryString != null && !"".equals(queryString)) {
            String[] split = queryString.split("&");
            for (String data : split) {
                String[] splitData = data.split("=");
                parameters.put(splitData[0], splitData[1]);
            }
        }
    }

    public String get(String key) {
        return parameters.get(key);
    }
}
