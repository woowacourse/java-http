package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_VALUE_LENGTH = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final HttpRequestFirstLine firstLine;
    private final HttpRequestHeader header;
    private final HttpCookie cookie;
    private final String body;

    public HttpRequest(BufferedReader reader) throws IOException {
        firstLine = new HttpRequestFirstLine(reader.readLine());
        header = new HttpRequestHeader(reader);
        cookie = new HttpCookie(header.getOrDefault("Cookie", ""));
        body = parseBody(reader);
    }

    public boolean isGet() {
        return firstLine.getMethod() == HttpMethod.GET;
    }

    public boolean isPost() {
        return firstLine.getMethod() == HttpMethod.POST;
    }

    public Map<String, String> parseQueryParameters() {
        Map<String, String> bodys = new HashMap<>();
        String[] pairs = body.split(PARAMETER_DELIMITER);
        for (String pair : pairs) {
            String[] keyAndValue = pair.split(KEY_VALUE_DELIMITER);
            if (keyAndValue.length == KEY_VALUE_LENGTH) {
                bodys.put(keyAndValue[KEY_INDEX], keyAndValue[VALUE_INDEX]);
            }
        }
        return bodys;
    }

    private String parseBody(BufferedReader reader) throws IOException {
        int contentLength = Integer.parseInt(header.getOrDefault("Content-Length", "0"));
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);

        return new String(buffer);
    }

    public String getUrl() {
        return firstLine.getUrl();
    }

    public HttpCookie getCookie() {
        return cookie;
    }

    public String getBody() {
        return body;
    }
}
