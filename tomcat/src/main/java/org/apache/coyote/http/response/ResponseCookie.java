package org.apache.coyote.http.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.catalina.session.Session;
import org.apache.coyote.http.HttpHeader;

public class ResponseCookie implements Assemblable {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE_DELIMITER = "; ";
    private static final String PREFIX = "";
    private static final String SUFFIX = " ";

    private final Map<String, String> cookies;

    protected ResponseCookie() {
        this.cookies = new HashMap<>();
    }

    protected void addSessionCookie(Session session) {
        cookies.put(JSESSIONID, session.getId());
    }

    @Override
    public void assemble(StringBuilder builder) {
        if (cookies.isEmpty()) {
            return;
        }
        builder.append("%s: ".formatted(HttpHeader.SET_COOKIE.value()))
                .append(cookies.entrySet()
                        .stream()
                        .map(this::convert)
                        .collect(Collectors.joining(COOKIE_DELIMITER, PREFIX, SUFFIX)))
                .append("\r\n");
    }

    private String convert(Entry<String, String> entry) {
        return "%s=%s".formatted(entry.getKey(), entry.getValue());
    }
}
