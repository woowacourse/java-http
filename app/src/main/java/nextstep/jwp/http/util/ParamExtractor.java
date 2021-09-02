package nextstep.jwp.http.util;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ParamExtractor {

    private static final String QUERY_STRING_PARAM_SEPARATOR = "&";

    private ParamExtractor() {
    }

    public static Map<String, String> extractParams(String query) {
        String[] splitParams = query.split(QUERY_STRING_PARAM_SEPARATOR);

        return Arrays.stream(splitParams)
            .map(param -> param.split("="))
            .filter(params -> params.length == 2)
            .collect(Collectors.toMap(param -> param[0], param -> param[1]));
    }
}
