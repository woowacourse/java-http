package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.response.UserRequest;
import org.apache.coyote.http11.exception.QueryParamNotFoundException;

public class QueryParam {

    private final Map<String, String> parameters;

    public QueryParam(final String path) {
        this.parameters = toParameters(path);
    }

    private Map<String, String> toParameters(final String path) {
        Map<String, String> parameters = new HashMap<>();

        String queryString = path.split("\\?")[1];
        for(String keyAndValue : queryString.split("&")) {
            String key = keyAndValue.split("=")[0];
            String value = keyAndValue.split("=")[1];

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
