package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.ContentType.ALL;
import static org.apache.coyote.http11.ContentType.HTML;
import static org.apache.coyote.http11.ContentType.URL_ENCODED;
import static org.apache.coyote.http11.Header.ACCEPT;
import static org.apache.coyote.http11.Header.CONTENT_TYPE;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.Header;

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
        if (URL_ENCODED.getType().equals(headers.get(CONTENT_TYPE.getName()))) {
            readBody();
        }
    }

    private void readBody() throws IOException {
        char[] chars = new char[getContentLength()];
        bufferedReader.read(chars, 0, getContentLength());
        putBody(new String(chars));
    }

    private void putBody(String line) {
        String[] split = line.split("&");
        for (String s : split) {
            String[] keyValue = s.split("=");
            bodies.put(keyValue[0], keyValue[1]);
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

    public int getContentLength() {
        return Integer.parseInt(headers.get(Header.CONTENT_LENGTH.getName()));
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

    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }

    public Map<String, String> getParams() {
        return requestUri.getParams();
    }

    public RequestUri getUriRequest() {
        return requestUri;
    }

    public String getMethod() {
        return requestUri.getMethod();
    }
}
