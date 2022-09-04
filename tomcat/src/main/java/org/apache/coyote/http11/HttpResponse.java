package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class HttpResponse {

    private final String responseStartLine;
    private final Map<String, String> responseHeaders;
    private String responseBody;

    public static HttpResponseBuilder ok() {
        return new HttpResponseBuilder("HTTP/1.1 200 OK \r\n");
    }

    public static HttpResponseBuilder redirect(final String url) {
        return new HttpResponseBuilder("HTTP/1.1 302 Found \r\n")
                .header("Location", url);
    }

    public static HttpResponseBuilder notFound() {
        return new HttpResponseBuilder("HTTP/1.1 404 Not Found \r\n");
    }

    HttpResponse(String startLine) {
        responseStartLine = startLine;
        responseHeaders = new HashMap<>();
        responseBody = "";
    }

    public void addBody(final String responseBody) {
        responseHeaders.put("Content-Length", String.valueOf(responseBody.getBytes().length));
        this.responseBody = responseBody;
    }

    public void addHeader(final String name, final String value) {
        responseHeaders.put(name, value);
    }

    public byte[] writeValueAsBytes() {
        return writeValueAsString().getBytes();
    }

    public String writeValueAsString() {
        String response = responseStartLine;
        for (String key : responseHeaders.keySet()) {
            response += key + ": " + responseHeaders.get(key) + " \r\n";
        }
        response += "\r\n";
        response += responseBody;
        return response;
    }

    public boolean hasBody() {
        return responseBody.length() > 0;
    }
}
