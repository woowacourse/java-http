package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.exception.UncheckedHttpException;
import org.apache.coyote.http11.component.HttpCookie;
import org.apache.coyote.http11.component.HttpHeaders;

public class HttpRequestHeader {

    private static final String HEADER_KEY_VALUE_DELIMITER = ": ";
    private static final int SPLIT_COOKIE_KEY_VALUE_SIZE = 2;

    private final Map<String, String> headers;

    private HttpRequestHeader(Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpRequestHeader from(List<String> lines) {
        Map<String, String> result = lines.stream()
                .map(HttpRequestHeader::parseHeaders)
                .collect(Collectors.toMap(split -> split[0], split -> split[1]));
        return new HttpRequestHeader(result);
    }

    private static String[] parseHeaders(String header) {
        String[] splitLine = header.split(HEADER_KEY_VALUE_DELIMITER);
        validateSplitLineSize(splitLine);
        return splitLine;
    }

    private static void validateSplitLineSize(String[] splitLine) {
        if (splitLine.length != SPLIT_COOKIE_KEY_VALUE_SIZE) {
            throw new UncheckedHttpException(new IllegalArgumentException("헤더 키와 값 형식이 잘못되었습니다."));
        }
    }

    public Session getSession(boolean needSession) {
        if (headers.containsKey(HttpHeaders.COOKIE)) {
            String value = headers.get(HttpHeaders.COOKIE);
            HttpCookie cookie = HttpCookie.from(value);
            SessionManager sessionManager = SessionManager.getInstance();
            Session session = sessionManager.findSession(cookie.getSessionValue());
            if (session != null) {
                return session;
            }
        }
        return needSession ? new Session() : null;
    }

    public String getValue(String key) {
        return headers.getOrDefault(key, null);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
