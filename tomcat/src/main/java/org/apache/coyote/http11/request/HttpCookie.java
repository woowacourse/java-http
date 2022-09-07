package org.apache.coyote.http11.request;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.catalina.HttpSession;
import org.apache.catalina.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpCookie {
    private static final Logger log = LoggerFactory.getLogger(HttpCookie.class);
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String REQUEST_COOKIE_NAME = "Cookie";
    private static final String RESPONSE_COOKIE_NAME = "Set-Cookie: ";
    private static final String SESSION_ID = "JSESSIONID";
    private static final String KET_VALUE_STANDARD = "=";

    private final Map<String, String> values;

    public HttpCookie() {
        values = new LinkedHashMap<>();
    }

    private HttpCookie(Map<String, String> values) {
        this.values = new LinkedHashMap(values);
    }

    public static HttpCookie extract(HttpHeaders httpHeaders) {
        String cookie = httpHeaders.get(REQUEST_COOKIE_NAME);
        log.info("cookie : {}", cookie);
        if (cookie == null) {
            return new HttpCookie();
        }
        Map<String, String> mp = new LinkedHashMap<>();
        var datas = cookie.split(" ");
        for (var data : datas) {
            mp.put(data.split(KET_VALUE_STANDARD)[KEY_INDEX], data.split(KET_VALUE_STANDARD)[VALUE_INDEX]);
        }
        return new HttpCookie(mp);
    }

    public static HttpCookie ofJSessionId(String id) {
        SessionManager sessionManager = new SessionManager();
        HttpSession session = sessionManager.findSession(id);
        return new HttpCookie(Map.of(SESSION_ID, session.getId()));
    }

    public String getResponse() {
        var sb = new StringBuilder();
        for(var entry : values.entrySet()){
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        log.info("getResponse : {}", sb);
        return sb.toString();
    }

    public String getSession() {
        log.info(SESSION_ID + ": {}", values.get(SESSION_ID));
        return values.get(SESSION_ID);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public Map<String, String> getValues() {
        log.info("GET VALUES : {}", values);
        return values;
    }

    public HttpCookie addCookie(HttpCookie cookie) {
        Map<String, String> response = cookie.getValues();
        this.values.putAll(response);
        return this;
    }
}
