package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpHeader {

    private static final String HOST = "Host";
    private static final String CONNECTION = "Connection";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String COOKIE = "Cookie";
    private static final String ACCEPT = "Accept";

    private final Map<String, String> values = new HashMap<>();

    public HttpHeader(final BufferedReader reader) throws IOException {
        String header;
        while (!(header = reader.readLine()).isBlank()) {
            final String[] splitHeader = header.split(": ");
            values.put(splitHeader[0], splitHeader[1]);
        }
    }

    public boolean containContentLength() {
        return values.containsKey(CONTENT_LENGTH);
    }

    public boolean notContainJsessionId() {
        final String cookie = values.get(COOKIE);
        if (cookie == null) {
            return true;
        }
        return !cookie.contains("JSESSIONID");
    }

    public String findJsessionId() {
        final String cookie = values.get(COOKIE);
        final Pattern pattern = Pattern.compile("JSESSIONID=([a-zA-Z0-9-]+)");
        final Matcher matcher = pattern.matcher(cookie);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public int getContentLength() {
        return Integer.parseInt(values.get(CONTENT_LENGTH));
    }
}
