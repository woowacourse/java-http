package nextstep.jwp.request;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryStringParameters {
    public static final String NON_EXIST_PARAMETER_EXCEPTION_MESSAGE = "존재하지 않는 파라미터 이름입니다.";
    private static final QueryStringParameters EMPTY_PARAMETERS = new QueryStringParameters(Collections.emptyMap());

    private final Map<String, String> parameters;

    public QueryStringParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public static QueryStringParameters of(String queryString) {
        if (Objects.nonNull(queryString)) {
            return new QueryStringParameters(extracted(queryString));
        }
        return EMPTY_PARAMETERS;
    }

    public static QueryStringParameters getEmptyParameters() {
        return EMPTY_PARAMETERS;
    }

    private static Map<String, String> extracted(String requestQueryString) {
        return Stream.of(requestQueryString.split("&"))
                .map(queryString -> queryString.split("="))
                .collect(Collectors.toMap(queryString -> queryString[0], queryString -> queryString[1]));
    }

    public String getParameter(String name) {
        String value = parameters.get(name);
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException(NON_EXIST_PARAMETER_EXCEPTION_MESSAGE);
        }
        return value;
    }

    public boolean existParameter() {
        return parameters.size() > 0;
    }
}
