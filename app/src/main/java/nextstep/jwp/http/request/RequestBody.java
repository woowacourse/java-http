package nextstep.jwp.http.request;

import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private static final RequestBody EMPTY = new RequestBody(new HashMap<>());
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> parameters;

    private RequestBody(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public static RequestBody parse(String body) {
        Map<String, String> parameters = new HashMap<>();

        String[] splitedQuerys = body.split("&");
        for (String query : splitedQuerys) {
            String[] splitedQuery = query.split("=");
            String key = splitedQuery[KEY_INDEX];
            String value = splitedQuery[VALUE_INDEX];

            parameters.put(key, value);
        }

        return new RequestBody(parameters);
    }

    public String getParameter(String parameter) {
        return parameters.get(parameter);
    }

    public static RequestBody empty() {
        return EMPTY;
    }
}
