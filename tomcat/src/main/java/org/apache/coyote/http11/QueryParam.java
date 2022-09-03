package org.apache.coyote.http11;

import java.util.Map;
import nextstep.jwp.request.UserRequest;
import org.apache.coyote.http11.exception.QueryParamNotFoundException;
import org.apache.coyote.http11.utils.PairConverter;

public class QueryParam {

    private static final int QUERY_PARAM = 1;
    private static final String KEY_VALUE_PAIR_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String QUERY_PARAM_DELIMITER = "\\?";

    private final Map<String, String> parameters;

    public QueryParam(final String path) {
        this.parameters = PairConverter.toMap(toQueryString(path), KEY_VALUE_PAIR_DELIMITER, KEY_VALUE_DELIMITER);
    }

    private String toQueryString(final String path) {
        return path.split(QUERY_PARAM_DELIMITER)[QUERY_PARAM];
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
