package nextstep.jwp.request;

import java.util.HashMap;
import java.util.Map;

public class QueryParameterExtractor {

    private static final String QUERY_PARAM_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final String NEWLINE = "\r\n";
    private QueryParameterExtractor() {
    }

    public static Map<String, String> extract(String request) {
        final Map<String, String> requestQueryString = new HashMap<>();
        for (String query : request.split(QUERY_PARAM_SEPARATOR)) {
            final String[] queriedValue = query.split(KEY_VALUE_SEPARATOR);
            requestQueryString.put(queriedValue[0], removeNewLine(queriedValue[1]));
        }
        return requestQueryString;
    }

    private static String removeNewLine(String input) {
        return input.replace(NEWLINE, "");
    }
}
