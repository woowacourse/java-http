package nextstep.jwp.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HttpRequest {

    private static final String BLANK_DELIMITER = " ";
    private static final int FIRST_WORD_INDEX = 0;
    private static final int SECOND_WORD_INDEX = 1;
    private static final String PATH_AND_QUERY_STRING_DELIMITER = "?";
    private static final int START_INDEX = 0;
    private static final String QUERY_STRING_DELIMITER = "&";
    private static final String KEY_AND_VALUE_DELIMITER = "=";

    private final String statusLine;
    private final String bodyLine;
    private List<String> headerLines;

    public HttpRequest(String statusLine, List<String> headerLines, String bodyLine) {
        this.statusLine = statusLine;
        this.headerLines = headerLines;
        this.bodyLine = bodyLine;
    }

    public boolean isEmptyLine() {
        return Objects.isNull(statusLine) && headerLines.isEmpty() && Objects.isNull(bodyLine);
    }

    public String extractURI() {
        return statusLine.split(BLANK_DELIMITER)[SECOND_WORD_INDEX];
    }

    public Map<String, String> extractURIQueryParams() {
        String uri = extractURI();
        Map<String, String> queryParams = new HashMap<>();
        int index = uri.indexOf(PATH_AND_QUERY_STRING_DELIMITER);
        if (index != -1) {
            String queryString = uri.substring(index + 1);
            String[] splitQueryStrings = queryString.split(QUERY_STRING_DELIMITER);

            for (String splitQueryString : splitQueryStrings) {
                String[] splitParam = splitQueryString.split(KEY_AND_VALUE_DELIMITER);
                String key = splitParam[0];
                String value = splitParam[1];
                queryParams.put(key, value);
            }
        }
        return queryParams;
    }

    public String extractURIPath() {
        String uri = extractURI();
        int queryStringDelimiterIndex = uri.indexOf(PATH_AND_QUERY_STRING_DELIMITER);
        if (queryStringDelimiterIndex != -1) {
            return uri.substring(START_INDEX, queryStringDelimiterIndex);
        }
        return uri;
    }

    public String extractHttpMethod() {
        return statusLine.split(BLANK_DELIMITER)[FIRST_WORD_INDEX];
    }

    public Map<String, String> extractFormData() {
        String[] splitFormData = bodyLine.split(QUERY_STRING_DELIMITER);
        Map<String, String> result = new HashMap<>();
        for (String s : splitFormData) {
            String[] split = s.split(KEY_AND_VALUE_DELIMITER);
            result.put(split[0], split[1]);
        }
        return result;
    }
}
