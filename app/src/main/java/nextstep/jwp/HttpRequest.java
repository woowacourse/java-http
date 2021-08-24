package nextstep.jwp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HttpRequest {

    private static final int FIRST_LINE_OF_HTTP_REQUEST = 0;
    private static final String BLANK_DELIMITER = " ";
    private static final int SECOND_WORD_INDEX = 1;
    private static final String PATH_AND_QUERY_STRING_DELIMITER = "?";
    private static final int START_INDEX = 0;
    private static final String QUERY_STRING_DELIMITER = "&";
    private static final String KEY_AND_VALUE_DELIMITER = "=";
    private List<String> requestLines = new ArrayList<>();

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while (reader.ready()) {
            requestLines.add(reader.readLine());
        }
    }

    public String extractURI() {
        if (requestLines.size() > 0) {
            String firstLine = requestLines.get(FIRST_LINE_OF_HTTP_REQUEST);
            String requestURI = firstLine.split(BLANK_DELIMITER)[SECOND_WORD_INDEX];
            return requestURI;
        }
        return null;
    }

    public Map<String, String> extractURIQueryParams() {
        String uri = extractURI();
        Map<String, String> queryParams = new HashMap<>();
        if (Objects.isNull(uri)) {
            return queryParams;
        }
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
        if (Objects.isNull(uri)) {
            return null;
        }
        int queryStringDelimiterIndex = uri.indexOf(PATH_AND_QUERY_STRING_DELIMITER);
        if (queryStringDelimiterIndex != -1) {
            return uri.substring(START_INDEX, queryStringDelimiterIndex);
        }
        return uri;
    }
}
