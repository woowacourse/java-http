package org.apache.coyote.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class HttpCookie implements Assemblable {

    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie create() {
        return new HttpCookie(new HashMap<>());
    }

    public void setJsessionid(String jsessionid) {
        cookies.put(JSESSIONID, jsessionid);
    }

    @Override
    public void assemble(StringBuilder builder) {
        if (cookies.isEmpty()) {
            return;
        }
        builder.append("Set-Cookie: ")
                .append(cookies.entrySet()
                        .stream()
                        .map(this::convert)
                        .collect(Collectors.joining("; ", "", " ")))
                .append("\r\n");
    }

    private String convert(Entry<String, String> entry) {
        return "%s=%s".formatted(entry.getKey(), entry.getValue());
    }
}
