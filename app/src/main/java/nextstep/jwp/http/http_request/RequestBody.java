package nextstep.jwp.http.http_request;

import nextstep.jwp.exception.NotFoundParamException;

import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private static final String PARAM_DELIMITER = "&";
    private static final String PARAM_KEY_AND_VALUE_DELIMITER = "=";

    private final Map<String, String> params;

    public RequestBody(String request) {
        this(parseRequests(request));
    }

    private RequestBody(Map<String, String> params) {
        this.params = params;
    }

    private static Map<String, String> parseRequests(String request) {
        if (request == null) {
            return null;
        }

        String[] jsonParams = request.split(PARAM_DELIMITER);
        return parseParams(jsonParams);
    }

    private static Map<String, String> parseParams(String[] queryParams) {
        Map<String, String> params = new HashMap<>();
        for (String queryParam : queryParams) {
            splitKeyAndValue(params, queryParam);
        }
        return params;
    }

    private static void splitKeyAndValue(Map<String, String> params, String queryParam) {
        String[] keyAndValue = queryParam.split(PARAM_KEY_AND_VALUE_DELIMITER);
        params.put(keyAndValue[0].trim(), keyAndValue[1].trim());
    }

    public String getParam(String key) {
        if (!params.containsKey(key)) {
            throw new NotFoundParamException(key);
        }

        return params.get(key);
    }
}
