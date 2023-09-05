package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.ContentType.HTML;
import static org.apache.coyote.http11.Header.ACCEPT;
import static org.apache.coyote.http11.Header.CONTENT_LENGTH;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.Header;

public class HttpRequest {

    private final BufferedReader bufferedReader;
    private final Map<String, String> headers = new HashMap<>();
    private RequestLine requestLine;
    private final Map<String, String> bodies = new HashMap<>();

    public HttpRequest(BufferedReader bufferedReader) throws IOException {
        this.bufferedReader = bufferedReader;
        read();
    }

    private void read() throws IOException {
        String line;
        requestLine = RequestLine.of(bufferedReader.readLine());

        while (!(line = bufferedReader.readLine()).isBlank()) {
            putHeader(line);
        }
        if (headers.containsKey(CONTENT_LENGTH.getName())) {
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
        String[] split = line.split(": ");
        String name = split[0];
        String content = split[1];
        headers.put(name, content);
    }

    public String getAccept() {
        return headers.getOrDefault(ACCEPT.getName(), HTML.getType());
    }

    public int getContentLength() {
        return Integer.parseInt(headers.get(Header.CONTENT_LENGTH.getName()));
    }

    public String getBodyValue(String key) {
        return bodies.get(key);
    }

    public boolean cookieKeyExists(String key) {
        if (!headers.containsKey(Header.COOKIE.getName())) {
            return false;
        }
        Cookie cookie = new Cookie(headers.get(Header.COOKIE.getName()));
        try {
            cookie.getValue(key);
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    public String getProtocolVersion() {
        return requestLine.getProtocolVersion();
    }

    public String getRequestUrl() {
        return requestLine.getUrl();
    }

    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }

    public Map<String, String> getParams() {
        return requestLine.getParams();
    }

    public RequestLine getUriRequest() {
        return requestLine;
    }

    public String getMethod() {
        return requestLine.getMethod();
    }
}
