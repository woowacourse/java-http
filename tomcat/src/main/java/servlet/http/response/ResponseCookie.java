package servlet.http.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class ResponseCookie implements Assemblable {

    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> cookies;

    private ResponseCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static ResponseCookie create() {
        return new ResponseCookie(new HashMap<>());
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
