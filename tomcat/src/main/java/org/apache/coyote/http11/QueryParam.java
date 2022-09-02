package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.request.UserRequest;
import org.apache.coyote.http11.exception.QueryParamNotFoundException;

public class QueryParam {

    private static final int QUERY_PARAM = 1;
    private static final int QUERY_PARAM_KEY = 0;
    private static final int QUERY_PARAM_VALUE = 1;
    private static final String KEY_VALUE_PAIR_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String QUERY_PARAM_DELIMITER = "\\?";

    private final Map<String, String> parameters;

    public QueryParam(final String path) {
        this.parameters = toParameters(path);
    }

    private Map<String, String> toParameters(final String path) {
        Map<String, String> parameters = new HashMap<>();

        String queryString = path.split(QUERY_PARAM_DELIMITER)[QUERY_PARAM];
        for(String keyAndValue : queryString.split(KEY_VALUE_PAIR_DELIMITER)) {
            String key = keyAndValue.split(KEY_VALUE_DELIMITER)[QUERY_PARAM_KEY];
            String value = keyAndValue.split(KEY_VALUE_DELIMITER)[QUERY_PARAM_VALUE];

            parameters.put(key, value);
        }

        return parameters;
    }

    private boolean matchParameters(String key) {
        return parameters.containsKey(key);
    }

    public UserRequest toUserRequest(String account, String password) {
        if (matchParameters(account) && matchParameters(password)) {
            return new UserRequest(parameters.get(account), parameters.get(password));
        }
        throw new QueryParamNotFoundException();
    }
}
