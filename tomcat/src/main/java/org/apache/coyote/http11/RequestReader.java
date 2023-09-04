package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.apache.coyote.http11.ContentType.ALL;
import static org.apache.coyote.http11.ContentType.HTML;
import static org.apache.coyote.http11.ContentType.URL_ENCODED;
import static org.apache.coyote.http11.Header.ACCEPT;
import static org.apache.coyote.http11.Header.CONTENT_LENGTH;
import static org.apache.coyote.http11.Header.CONTENT_TYPE;
import static org.apache.coyote.http11.Header.COOKIE;

public class RequestReader {

    private static final String CHARSET_UTF_8 = ";charset=utf-8";
    private static final String AMPERSAND = "&";
    private static final String EQUAL_SIGN = "=";
    private static final String CRLF = "\r\n";
    private static final String SPACE = " ";
    private static final String COMMA = ",";
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
        int contentLength = getContentLength();
        char[] chars = new char[contentLength];
        bufferedReader.read(chars, 0, contentLength);
        putBody(new String(chars));
    }

    public int getContentLength() {
        return Integer.parseInt(headers.get(CONTENT_LENGTH.getName()));
    }

    private void putBody(String line) {
        String[] split = line.split(AMPERSAND);
        for (String s : split) {
            String[] keyValue = s.split(EQUAL_SIGN);
            bodies.put(keyValue[0], keyValue[1]);
        }
    }

    public void putHeader(String line) {
        if (line.endsWith(CRLF)) {
            return;
        }
        String[] split = line.split(SPACE);
        String key = split[0].substring(0, split[0].length() - 1);
        String value = split[1];
        headers.put(key, value);
    }

    public String getContentType() {
        String accept = headers.getOrDefault(ACCEPT.getName(), HTML.getType());
        if (!accept.contains(COMMA)) {
            return accept + CHARSET_UTF_8;
        }
        String[] split = accept.split(COMMA);
        if (split[0].equals(ALL.getType())) {
            return HTML.getType() + CHARSET_UTF_8;
        }
        return split[0] + CHARSET_UTF_8;
    }

    public String getBodyValue(String key) {
        return bodies.get(key);
    }

    public boolean isSessionExits() {
        if (headers.containsKey(COOKIE.getName())) {
            return true;
        }
        Cookie cookie = new Cookie(headers.get(COOKIE.getName()));
        try {
            cookie.getSession();
        } catch (NullPointerException e) {
            return false;
        }
        return true;
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

    public Map<String, String> getParams() {
        return requestUri.getParams();
    }
}
