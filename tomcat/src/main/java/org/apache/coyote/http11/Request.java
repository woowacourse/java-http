package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Request {
    private static final Pattern ACCEPT_TYPE_PATTERN = Pattern.compile("Accept: (.+?)\r\n");

    private final String acceptType;
    private final RequestUri requestUri;

    private Request(final String acceptType, final RequestUri requestUri) {
        this.acceptType = acceptType;
        this.requestUri = requestUri;
    }

    public static Request from(final BufferedReader bufferedReader) throws IOException {
        final String request = extractRequestContent(bufferedReader);
        return new Request(extractAccessType(request), RequestUri.from(request));
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

    private static String extractAccessType(final String request) {
        final Matcher acceptTypeMatcher = ACCEPT_TYPE_PATTERN.matcher(request);
        String acceptType = "";
        if (acceptTypeMatcher.find()) {
            acceptType = acceptTypeMatcher.group(1);
        }
        return acceptType;
    }

    public String getAccessType() {
        return acceptType;
    }

    public String getPath() {
        final String path = requestUri.getPath();
        if (path.equals("/")) {
            return "/index.html";
        }
        if (acceptType.startsWith("text/html") && !path.endsWith(".html")) {
            return path + ".html";
        }
        return path;
    }

    public Map<String, String> getQueryParams() {
        return requestUri.getQueryParams();
    }
}
