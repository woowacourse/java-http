package servlet.http.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import servlet.http.HttpHeader;

public class ResponseCookie implements Assemblable {

    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> cookies;

    protected ResponseCookie() {
        this(new HashMap<>());
    }

    private ResponseCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    protected void setJsessionid(String jsessionid) {
        cookies.put(JSESSIONID, jsessionid);
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
                        .collect(Collectors.joining("; ", "", " ")))
                .append("\r\n");
    }

    private String convert(Entry<String, String> entry) {
        return "%s=%s".formatted(entry.getKey(), entry.getValue());
    }
}
