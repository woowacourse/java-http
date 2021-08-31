package nextstep.jwp.http.request;

import com.google.common.base.Strings;
import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private final int KEY_INDEX = 0;
    private final int VALUE_INDEX = 1;
    private final int PARAM_LENGTH = 2;

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
            String[] param = parameter.split("=");
            if (param.length == PARAM_LENGTH) {
                params.put(param[KEY_INDEX], param[VALUE_INDEX]);
            }
        }
    }

    public String getParam(String key) {
        return params.get(key);
    }
}
