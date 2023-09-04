package org.apache.coyote.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.header.HttpMethod;

public class RequestExtractor {

    private static final int START_LINE_INDEX = 0;
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int TARGET_PATH_INDEX = 1;

    public static HttpMethod extractHttpMethod(final String request) {
        String[] startLineInfos = extractStartLineInfos(request);
        String httpMethod = startLineInfos[HTTP_METHOD_INDEX];
        return HttpMethod.from(httpMethod);
    }

    private static String[] extractStartLineInfos(final String request) {
        String[] requestLines = request.split("\r\n");
        String requestStartLine = requestLines[START_LINE_INDEX];
        return requestStartLine.split(" ");
    }

    public static String extractTargetPath(final String request) {
        String[] startLineInfos = extractStartLineInfos(request);
        return startLineInfos[TARGET_PATH_INDEX];
    }

    public static Map<String, String> extractQueryParam(String request) {
        Map<String, String> result = new HashMap<>();

        String[] startLineInfos = extractStartLineInfos(request);
        String totalTargetPath = startLineInfos[TARGET_PATH_INDEX];
        int startIndexOfQueryString = totalTargetPath.indexOf("?");
        String queryString = totalTargetPath.substring(startIndexOfQueryString + 1);
        String[] params = queryString.split("&");

        for (String param : params) {
            int indexOfSplitParam = param.indexOf("=");
            String key = param.substring(0, indexOfSplitParam);
            String value = param.substring(indexOfSplitParam + 1);
            result.put(key, value);
        }
        return result;
    }
}
