package nextstep.jwp.http.request;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.http.HttpCookie;

public class RequestHeader {

    private static final String HEADER_DELIMITER = ": ";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Cookie";
    private static final String COOKIES_DELIMITER = "; ";
    private static final String COOKIE_DELIMITER = "=";

    private final Map<String, String> header;
    private final HttpCookie httpCookie;

    public RequestHeader() {
        this(new ConcurrentHashMap<>(), new HttpCookie());
    }

    private RequestHeader(Map<String, String> header, HttpCookie httpCookie) {
        this.httpCookie = httpCookie;
        this.header = header;
    }

    public void setHeader(String line) {
        String[] splitLine = line.split(HEADER_DELIMITER);
        String key = splitLine[0];
        String value = splitLine[1];

        if (!COOKIE.equals(key)) {
            header.put(key, value);
            return;
        }
        header.put(key, "true");
        setHttpCookie(value);
    }

    private void setHttpCookie(String value) {
        String[] splitCookies = value.split(COOKIES_DELIMITER);

        for (String each : splitCookies) {
            String[] splitCookie = each.split(COOKIE_DELIMITER);
            httpCookie.setCookie(splitCookie[0], splitCookie[1]);
        }
    }

    public boolean isContentLength() {
        return header.containsKey(CONTENT_LENGTH);
    }

    public boolean getHttpCookie() {
        return header.containsKey(COOKIE);
    }

    public String getValue(String key) {
        return header.get(key);
    }

    public Map<String, String> getHeader() {
        return header;
    }
}
