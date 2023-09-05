package org.apache.coyote.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestParser {

    private static final String ACCEPT_HEADER = "Accept: ";
    private static final String CONTENT_TYPE = "Content-Type: ";
    private static final String SPLIT_HEADER_DELIMITER = " ";
    private static final String FINISH_SPLIT_DELIMITER = ";";
    private static final String SPLIT_VALUE_DELIMITER = ",";
    private static final String QUERY_STRING_SPLIT_DELIMITER = "\\?";
    private static final String QUERY_STRING_KEY_PAIR_SPLIT_DELIMITER = "&";

    private static final int RESOURCE_INDEX = 1;
    private static final int FINISH_RESOURCE_INDEX = 0;
    public static final String QUERY_STRING_KEY_VALUE_SPLIT_DELIMITER = "=";

    private final BufferedReader bufferedReader;

    public RequestParser(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        this.bufferedReader = new BufferedReader(inputStreamReader);
    }

    public Request getResource() throws IOException {
        RequestUrl requestUrl = readUrl(bufferedReader.readLine());
        List<RequestContentType> requestContentType = getResourceType();

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
        return message.split(SPLIT_HEADER_DELIMITER)[RESOURCE_INDEX]
                .split(FINISH_SPLIT_DELIMITER)[FINISH_RESOURCE_INDEX];
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

    private List<RequestContentType> getResourceType() throws IOException {
        String header;
        while ((header = bufferedReader.readLine()) != null) {
            if (header.startsWith(ACCEPT_HEADER) || header.startsWith(CONTENT_TYPE)) {
                String resourceType = readLine(header);
                return Arrays.stream(resourceType.split(SPLIT_VALUE_DELIMITER))
                        .map(RequestContentType::findResourceType)
                        .collect(Collectors.toList());
            }
        }
        return List.of(RequestContentType.HTML);
    }
}
