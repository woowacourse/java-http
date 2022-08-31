package org.apache.coyote.http11;

import java.util.Map;
import java.util.TreeMap;

public class HttpResponse {

    private final String responseStartLine;
    private final Map<String, String> responseHeaders;
    private String responseBody;

    public static HttpResponse ok() {
        return new HttpResponse("HTTP/1.1 200 OK \r\n");
    }

    public static HttpResponse redirect() {
        return new HttpResponse("HTTP/1.1 302 Found \r\n");
    }

    private HttpResponse(String startLine) {
        responseStartLine = startLine;
        responseHeaders = new TreeMap<>();
        responseBody = "";
    }

    public void addBody(final String responseBody) {
        responseHeaders.put("Content-Length", String.valueOf(responseBody.getBytes().length));
        this.responseBody = responseBody;
    }

    public void addHeader(final String name, final String value) {
        responseHeaders.put(name, value);
    }

    public byte[] getBytes() {
        return getString().getBytes();
    }

    public String getString() {
        String response = responseStartLine;
        for (String key : responseHeaders.keySet()) {
            response += key + ": " + responseHeaders.get(key) + " \r\n";
        }
        response += "\r\n";
        response += responseBody;
        return response;
    }
}
