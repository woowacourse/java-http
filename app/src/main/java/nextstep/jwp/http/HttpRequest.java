package nextstep.jwp.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class HttpRequest {

    private static final int FIRST_LINE_OF_HTTP_REQUEST = 0;
    private static final String BLANK_DELIMITER = " ";
    private static final int FIRST_WORD_INDEX = 0;
    private static final int SECOND_WORD_INDEX = 1;
    private static final String PATH_AND_QUERY_STRING_DELIMITER = "?";
    private static final int START_INDEX = 0;
    private static final String QUERY_STRING_DELIMITER = "&";
    private static final String KEY_AND_VALUE_DELIMITER = "=";

    private List<String> requestLines = new ArrayList<>();

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            requestLines.add(line);
            line = bufferedReader.readLine();
            if (Objects.isNull(line)) {
                break;
            }
        }
        int contentLength = parseContentLength(requestLines);
        if (contentLength == 0) {

        }
        if (contentLength != 0) {
            try {
                char[] buffer = new char[contentLength];
                bufferedReader.read(buffer, 0, contentLength);
                String s = new String(buffer);
                requestLines.add(s);
            } catch (IOException exception) {

            }
        }
    }

    private int parseContentLength(List<String> httpRequestHeaders) {
        Optional<String> contentLengthHeader = httpRequestHeaders.stream()
            .filter(header -> header.startsWith("Content-Length:"))
            .findAny();
        if (contentLengthHeader.isEmpty()) {
            return 0;
        }
        String[] split = contentLengthHeader.orElseThrow(IllegalStateException::new)
            .split(": ");
        if (split.length != 2) {
            throw new IllegalStateException("Invalid Content Length");
        }
        return Integer.parseInt(split[1]);
    }

    public boolean isEmptyLine() {
        return requestLines.size() == 0;
    }

    public String extractURI() {
        String firstLine = requestLines.get(FIRST_LINE_OF_HTTP_REQUEST);
        String requestURI = firstLine.split(BLANK_DELIMITER)[SECOND_WORD_INDEX];
        return requestURI;
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
        String firstLine = requestLines.get(FIRST_LINE_OF_HTTP_REQUEST);
        return firstLine.split(BLANK_DELIMITER)[FIRST_WORD_INDEX];
    }

    public Map<String, String> extractFormData() {
        String lastLine = requestLines.get(requestLines.size() - 1);
        String[] splitFormData = lastLine.split("&");
        Map<String, String> result = new HashMap<>();
        for (String s : splitFormData) {
            String[] split = s.split("=");
            result.put(split[0], split[1]);
        }
        return result;
    }
}
