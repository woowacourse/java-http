package nextstep.jwp.ui.request;

import java.util.HashMap;
import java.util.Map;

public class Parameters {
    private Map<String, String> parameters;

    public Parameters() {
        this.parameters = new HashMap<>();
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
            parameters.put(splitData[0], splitData[1]);
        }
    }

    public String get(String key) {
        return parameters.get(key);
    }
}
