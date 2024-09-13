package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

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
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] keyAndValue = pair.split("=");
            if (keyAndValue.length == 2) {
                bodys.put(keyAndValue[0], keyAndValue[1]);
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
