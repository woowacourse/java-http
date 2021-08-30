package nextstep.jwp.http.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import nextstep.jwp.exception.EmptyQueryParametersException;
import nextstep.jwp.exception.QueryParameterNotFoundException;

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

    public static RequestBody empty() {
        return EMPTY;
    }

    public String getParameter(String parameter) {
        validateEmpty();
        validateExistQuery(parameter);

        return parameters.get(parameter);
    }

    private void validateEmpty() {
        if (parameters.isEmpty()) {
            throw new EmptyQueryParametersException();
        }
    }

    private void validateExistQuery(String parameter) {
        if (!parameters.containsKey(parameter)) {
            throw new QueryParameterNotFoundException();
        }
    }

    @Override
    public String toString() {
        StringJoiner stringJoiner = new StringJoiner("&");

        for (Entry<String, String> entry : parameters.entrySet()) {
            String parameter = String.format("%s=%s", entry.getKey(), entry.getValue());
            stringJoiner.add(parameter);
        }

        return stringJoiner.toString();
    }
}
