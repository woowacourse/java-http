package org.apache.coyote.util;

import org.apache.coyote.HttpMethod;

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
}
