package nextstep.jwp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestExtractor {

    private static final int FIRST_LINE_OF_HTTP_REQUEST = 0;
    private static final String BLANK_DELIMITER = " ";
    private static final int SECOND_WORD_INDEX = 1;
    private static final String PATH_AND_QUERY_STRING_DELIMITER = "?";
    private static final int START_INDEX = 0;
    private static final String QUERY_STRING_DELIMITER = "&";
    private static final String KEY_AND_VALUE_DELIMITER = "=";
    private List<String> requestLines = new ArrayList<>();

    public HttpRequestExtractor(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while (reader.ready()) {
            requestLines.add(reader.readLine());
        }
    }

    public String extractURI() {
        String firstLine = requestLines.get(FIRST_LINE_OF_HTTP_REQUEST);
        String requestURI = firstLine.split(BLANK_DELIMITER)[SECOND_WORD_INDEX];
        return requestURI;
    }

    public Map<String, String> extractURIQueryString() {
        String uri = extractURI();
        int index = uri.indexOf(PATH_AND_QUERY_STRING_DELIMITER);
        String queryString = uri.substring(index + 1);
        String[] splitQueryStrings = queryString.split(QUERY_STRING_DELIMITER);
        Map<String, String> queryParams = new HashMap<>();
        for (String splitQueryString : splitQueryStrings) {
            String[] splitParam = splitQueryString.split(KEY_AND_VALUE_DELIMITER);
            String key = splitParam[0];
            String value = splitParam[1];
            queryParams.put(key, value);
        }
        return queryParams;
    }

    public String extractURIPath() {
        String uri = extractURI();
        int queryStringDelimiterIndex = uri.indexOf(PATH_AND_QUERY_STRING_DELIMITER);
        return uri.substring(START_INDEX, queryStringDelimiterIndex);
    }
}
