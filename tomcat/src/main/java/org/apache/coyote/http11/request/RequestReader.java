package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Header;

public class RequestReader {

    private static final String CHARSET_UTF_8 = ";charset=utf-8";
    private final BufferedReader bufferedReader;
    private final Map<String, String> headers = new HashMap<>();
    private RequestUri requestUri;

    public RequestReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    public void read() throws IOException {
        String line;
        requestUri = RequestUri.of(bufferedReader.readLine());

        while (!(line = bufferedReader.readLine()).isBlank()) {
            putHeader(line);
        }
    }

    public void putHeader(String line) {
        String[] split = line.split(" ");
        String name = split[0].substring(0, split[0].length() - 1);
        String content = split[1];
        headers.put(name, content);
    }

    public String getContentType() {
        String accept = headers.getOrDefault(Header.ACCEPT.getName(), ContentType.HTML.getType());
        if (!accept.contains(",")) {
            return accept + CHARSET_UTF_8;
        }
        String[] split = accept.split(",");
        if (split[0].equals(ContentType.ALL.getType())) {
            return ContentType.HTML.getType() + CHARSET_UTF_8;
        }
        return split[0] + CHARSET_UTF_8;
    }

    public String getProtocol() {
        return requestUri.getProtocol();
    }

    public String getRequestUrl() {
        return requestUri.getUrl();
    }

    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }

    public Map<String, String> getParams() {
        return requestUri.getParams();
    }

    public RequestUri getUriRequest() {
        return requestUri;
    }

    public boolean isPost() {
        return this.requestUri.getMethod().equals("POST");
    }
}
