package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.apache.coyote.http11.ContentType.ALL;
import static org.apache.coyote.http11.ContentType.HTML;
import static org.apache.coyote.http11.Header.ACCEPT;

public class RequestReader {

    private static final String CHARSET_UTF_8 = ";charset=utf-8";
    private final BufferedReader bufferedReader;
    private final Map<String, String> headers = new HashMap<>();
    private RequestUri requestUri;
    private final Map<String, String> bodies = new HashMap<>();

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
        if (line.isEmpty()) {
            return;
        }
        String[] split = line.split(" ");
        String name = split[0].substring(0, split[0].length() - 1);
        String content = split[1];
        headers.put(name, content);
    }

    public String getContentType() {
        String accept = headers.getOrDefault(ACCEPT.getName(), HTML.getType());
        if (!accept.contains(",")) {
            return accept + CHARSET_UTF_8;
        }
        String[] split = accept.split(",");
        if (split[0].equals(ALL.getType())) {
            return HTML.getType() + CHARSET_UTF_8;
        }
        return split[0] + CHARSET_UTF_8;
    }

    public String getBodyValue(String key) {
        return bodies.get(key);
    }

    public String getProtocol() {
        return requestUri.getProtocol();
    }

    public String getRequestUrl() {
        return requestUri.getUrl();
    }

    public String getMethod() {
        return requestUri.getMethod();
    }
}
