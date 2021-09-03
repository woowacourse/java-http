package nextstep.jwp.framework.http.common;

import static nextstep.jwp.framework.http.request.HttpRequest.LINE_DELIMITER;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;
import nextstep.jwp.framework.http.session.HttpCookie;
import nextstep.jwp.framework.http.session.HttpSession;
import nextstep.jwp.framework.http.session.HttpSessions;

public class HttpHeaders {

    public static final String HEADER_DELIMITER = ": ";
    public static final String SPACE = " ";
    private static final String SESSION_ID = "JSESSIONID";
    private static final int HEADER_KEY_INDEX = 0;
    private static final int VALUE_KEY_INDEX = 1;

    private final Map<String, String> headers;
    private HttpCookie cookie;

    public HttpHeaders(final String lines) {
        this.headers = convert(lines);
        this.cookie = new HttpCookie();
        addCookie();
    }

    private Map<String, String> convert(final String lines) {
        final Map<String, String> result = new HashMap<>();
        final String[] headers = lines.split(LINE_DELIMITER);

        for (final String header : headers) {
            final String[] split = header.split(HEADER_DELIMITER);

            putAcceptHeader(result, split[HEADER_KEY_INDEX].trim(), split[VALUE_KEY_INDEX].trim());
        }

        return result;
    }

    private void putAcceptHeader(final Map<String, String> headers, final String key, final String value) {
        if (key.equals("Accept")) {
            final String[] types = value.split(",");

            putContentType(headers, types);
            return;
        }
        headers.put(key, value);
    }

    private void putContentType(final Map<String, String> headers, final String[] types) {
        for (String type : types) {
            putTextType(headers, type);
        }
    }

    private void putTextType(final Map<String, String> headers, final String type) {
        if (type.split("/")[0].equals("text")) {
            headers.put("Content-Type", type);
        }
    }

    public void setContentLength(final int length) {
        headers.remove("Content-Length");
        headers.put("Content-Length", "" + length);
    }

    public int contentLength() {
        return Integer.parseInt(headers.get("Content-Length"));
    }

    public void addCookie() {
        final String cookies = headers.get("Cookie");

        if (Objects.isNull(cookies)) {
            return;
        }
        cookie.addCookies(cookies);
    }

    public boolean hasCookie() {
        return Objects.nonNull(cookie.get(SESSION_ID)) && HttpSessions.isLoggedIn(cookie.get(SESSION_ID));
    }

    public HttpSession sessions() {
        String id = cookie.get(SESSION_ID);

        if (Objects.isNull(id)) {
            id = createId();
        }

        return HttpSessions.getSession(id);
    }

    private String createId() {
        final String uuid = UUID.randomUUID().toString();

        cookie.addCookie(SESSION_ID, uuid);

        return uuid;
    }

    public void setCookie(final String id) {
        headers.remove("Cookie");
        headers.put("Cookie", id);
    }

    public String convertHeaderToResponse() {
        final StringBuilder builder = new StringBuilder();

        for (Entry<String, String> pair : headers.entrySet()) {
            builder.append(pair.getKey())
                .append(HEADER_DELIMITER)
                .append(pair.getValue())
                .append("\r\n");
        }
        //builder.append(cookieToString());

        return builder.toString();
    }

    private String cookieToString() {
        return cookie.toString();
    }
}
