package nextstep.jwp.http.request;

import com.google.common.base.Strings;

import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String PARAM_DELIMITER = "&";

    private final Map<String, String> params = new HashMap<>();

    public RequestBody(String content) {
        addParams(content);
    }

    private void addParams(String content) {
        String[] tokens = content.split(PARAM_DELIMITER);
        for (String token : tokens) {
            if (Strings.isNullOrEmpty(token)) {
                continue;
            }
            String[] tmp = token.split(KEY_VALUE_DELIMITER);
            if (tmp.length == 2) {
                this.params.put(tmp[0], tmp[1]);
            }
        }
    }

    public String getParam(String key) {
        return params.get(key);
    }
}
