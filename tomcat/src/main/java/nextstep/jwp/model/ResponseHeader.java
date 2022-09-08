package nextstep.jwp.model;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseHeader {

    private final Map<String, String> headers = new HashMap<>();

    public ResponseHeader() {
    }

    public String getResponse() {
        return headers.entrySet().stream()
                .map(it -> it.getKey() + ": " + it.getValue() + " ")
                .collect(Collectors.joining("\r\n"));
    }

    public void addHeader(final String name, final String value) {
        headers.put(name, value);
    }

    public void addContentType(final String contentType) {
        if (headers.containsKey("Content-Type")) {
            headers.put("Content-Type", headers.get("Content-Type") + ";" + contentType);
            return;
        }
        headers.put("Content-Type", contentType);
    }

    public void setCookie(final String cookie) {
        headers.put("Set-Cookie", cookie);
    }
}
