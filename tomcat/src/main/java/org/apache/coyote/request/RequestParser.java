package org.apache.coyote.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class RequestParser {

    private static final String ACCEPT_HEADER = "Accept: ";
    private static final String CONTENT_TYPE = "Content-Type: ";
    private static final String SPLIT_HEADER_DELIMITER = " ";
    private static final String FINISH_SPLIT_DELIMITER = ";";
    private static final String SPLIT_VALUE_DELIMITER = ",";
    private static final String QUERY_STRING_SPLIT_DELIMITER = "\\?";
    private static final String QUERY_STRING_KEY_PAIR_SPLIT_DELIMITER = "&";
    private static final String QUERY_STRING_KEY_VALUE_SPLIT_DELIMITER = "=";

    private final BufferedReader bufferedReader;

    public RequestParser(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        this.bufferedReader = new BufferedReader(inputStreamReader);
    }

    public Request parse() throws IOException {
        RequestUrl requestUrl = readUrl(bufferedReader.readLine());
        RequestContentType requestContentType = getResourceType();

        return new Request(requestUrl, requestContentType);
    }

    private RequestUrl readUrl(String message) {
        String pathLine = readLine(message);

        String requestPath = pathLine.split(QUERY_STRING_SPLIT_DELIMITER)[0];
        if (hasNoQueryParameter(pathLine)) {
            return RequestUrl.of(requestPath, new HashMap<>());
        }

        Map<String, String> requestQueryString = getRequestQueryString(pathLine);
        return RequestUrl.of(requestPath, requestQueryString);
    }

    private String readLine(String message) {
        return message.split(SPLIT_HEADER_DELIMITER)[1]
                .split(FINISH_SPLIT_DELIMITER)[0];
    }

    private boolean hasNoQueryParameter(String pathLine) {
        return pathLine.split(QUERY_STRING_KEY_VALUE_SPLIT_DELIMITER).length == 1;
    }

    private Map<String, String> getRequestQueryString(String pathLine) {
        String queryStrings = pathLine.split(QUERY_STRING_SPLIT_DELIMITER)[1];
        Map<String, String> requestQueryString = new HashMap<>();
        for (String queryString : queryStrings.split(QUERY_STRING_KEY_PAIR_SPLIT_DELIMITER)) {
            String key = queryString.split(QUERY_STRING_KEY_VALUE_SPLIT_DELIMITER)[0];
            String value = queryString.split(QUERY_STRING_KEY_VALUE_SPLIT_DELIMITER)[1];
            requestQueryString.put(key, value);
        }
        return requestQueryString;
    }

    private RequestContentType getResourceType() throws IOException {
        String header;
        while ((header = bufferedReader.readLine()) != null) {
            if (header.startsWith(ACCEPT_HEADER) || header.startsWith(CONTENT_TYPE)) {
                String resourceType = readLine(header);
                return RequestContentType.findResourceType(resourceType.split(SPLIT_VALUE_DELIMITER)[0]);
            }
        }
        return RequestContentType.HTML;
    }
}
