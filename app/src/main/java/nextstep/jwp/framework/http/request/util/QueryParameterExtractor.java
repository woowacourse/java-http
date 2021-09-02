package nextstep.jwp.framework.http.request.util;

import java.util.HashMap;
import java.util.Map;

import static nextstep.jwp.framework.http.common.Constants.BLANK;
import static nextstep.jwp.framework.http.common.Constants.NEWLINE;

public class QueryParameterExtractor {

    private static final String QUERY_PARAM_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";

    private QueryParameterExtractor() {
    }

    public static Map<String, String> extract(final String request) {
        final Map<String, String> requestQueryString = new HashMap<>();
        for (String query : request.split(QUERY_PARAM_SEPARATOR)) {
            final String[] queriedValue = query.split(KEY_VALUE_SEPARATOR, 2);
            requestQueryString.put(queriedValue[0], removeNewLine(queriedValue[1]));
        }
        return requestQueryString;
    }

    private static String removeNewLine(final String input) {
        return input.replace(NEWLINE, BLANK);
    }
}
