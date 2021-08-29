package nextstep.jwp.http.request;

import com.google.common.base.Strings;
import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private final Map<String, String> params = new HashMap<>();

    public RequestBody(String requestBody) {
        initParams(requestBody);
    }

    private void initParams(String requestBody) {
        String[] parameters = requestBody.split("&");
        for (String parameter : parameters) {
            if (Strings.isNullOrEmpty(parameter)) {
                continue;
            }
            String[] temp = parameter.split("=");
            if (temp.length == 2) {
                params.put(temp[0], temp[1]);
            }
        }
    }

    public Map<String, String> getParams() {
        return params;
    }
}
