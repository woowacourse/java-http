package nextstep.jwp.http.request;

import com.google.common.base.Strings;

import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private Map<String, String> params = new HashMap<>();

    public RequestBody(String content) {
        addParams(content);
    }

    private void addParams(String content) {
        String[] tokens = content.split("&");
        for (String token : tokens) {
            if (Strings.isNullOrEmpty(token)) {
                continue;
            }
            String[] tmp = token.split("=");
            if (tmp.length == 2) {
                this.params.put(tmp[0], tmp[1]);
            }
        }
    }

    public String getParam(String key) {
        return params.get(key);
    }
}
