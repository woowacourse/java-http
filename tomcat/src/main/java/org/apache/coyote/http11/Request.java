package org.apache.coyote.http11;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Request {
    private static final Logger log = LoggerFactory.getLogger(Request.class);
    private static final Pattern ACCEPT_TYPE_PATTERN = Pattern.compile("Accept: (.+?)\r\n");
    private static final Pattern REQUEST_URI_PATTERN = Pattern.compile("GET (.+?) ");

    private final String acceptType;
    private final String uri;
    private final Map<String, String> queryParams;

    private Request(final String acceptType, final String uri, final Map<String, String> queryParams) {
        this.acceptType = acceptType;
        this.uri = uri;
        this.queryParams = queryParams;
    }

    public static Request from(final BufferedReader bufferedReader) throws IOException {
        final String request = extractRequestContent(bufferedReader);

        return new Request(getAcceptType(request), getUri(request), getQueryParams(request));
    }

    private static String extractRequestContent(final BufferedReader bufferedReader) throws IOException {
        final StringBuilder requestHeader = new StringBuilder();
        while (true) {
            final String line = bufferedReader.readLine();
            if (line == null || "".equals(line)) {
                break;
            }
            requestHeader.append(line).append("\r\n");
        }
        return requestHeader.toString();
    }

    private static String getAcceptType(final String request) {
        final Matcher acceptTypeMatcher = ACCEPT_TYPE_PATTERN.matcher(request);
        String acceptType = "";
        if (acceptTypeMatcher.find()) {
            acceptType = acceptTypeMatcher.group(1);
        }
        return acceptType;
    }

    private static String getUri(final String request) {
        final Matcher requestUriMatcher = REQUEST_URI_PATTERN.matcher(request);
        String uri = "";
        if (requestUriMatcher.find()) {
            uri = requestUriMatcher.group(1);
            int index = uri.indexOf("?");
            if (index != -1) {
                return uri.substring(0, index);
            }
            return uri;
        }
        return uri;
    }

    private static Map<String, String> getQueryParams(final String request) {
        int index = request.indexOf("?");
        if (index == -1) {
            return Collections.emptyMap();
        }
        final Map<String, String> result = new HashMap<>();
        final StringTokenizer queryParam = new StringTokenizer(request.substring(index + 1), "&");
        while (queryParam.hasMoreTokens()) {
            final String param = queryParam.nextToken();
            final String[] split = param.split("=");
            result.put(split[0], split[1]);
        }
        return result;
    }

    public String getAcceptType() {
        return acceptType;
    }

    public String getUri() {
        if (uri.equals("/")) {
            return "/index.html";
        }
        if (acceptType.startsWith("text/html") && !uri.endsWith(".html")) {
            return uri + ".html";
        }
        return uri;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}
