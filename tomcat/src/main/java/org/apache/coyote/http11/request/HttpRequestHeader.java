package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestHeader {
    private final Map<String, String> headers;

    public static HttpRequestHeader from(final BufferedReader bufferedReader) throws IOException {
        Map<String, String> parsedHeaders = new HashMap<>();
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            String[] headerTokens = line.split(": ");
            parsedHeaders.put(headerTokens[0], headerTokens[1]);
            line = bufferedReader.readLine();
        }
        return new HttpRequestHeader(parsedHeaders);
    }

    private HttpRequestHeader(final Map<String, String> headers) {
        this.headers = headers;
    }

    public HttpCookie getCookie() {
        return HttpCookie.from(this.headers.get("Cookie"));
    }

    public String getAccept() {
        return this.headers.get("Accept");
    }

    public String getContentLength() {
        return this.headers.get("Content-Length");
    }

}
