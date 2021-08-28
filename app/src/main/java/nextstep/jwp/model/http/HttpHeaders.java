package nextstep.jwp.model.http;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

public class HttpHeaders {
    private final Map<String, String> headers = new LinkedHashMap<>();
    private HttpProtocol protocol;

    public void add(String name, String value) {
        headers.put(name, value);
    }

    public void add(HttpHeaderType name, String value) {
        headers.put(name.value(), value);
    }

    public void addProtocol(HttpProtocol protocol) {
        this.protocol = protocol;
    }

    public String getHeader(HttpHeaderType name) {
        return headers.get(name.value());
    }

    public String getAllHeaders() {
        StringJoiner stringJoiner = new StringJoiner("\r\n");
        stringJoiner.add(HttpProtocol.NAME + " " + protocol.getProtocol() + " ");
        addAllHeaders(stringJoiner);
        return stringJoiner.toString();
    }

    private void addAllHeaders(StringJoiner stringJoiner) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String httpHeader = entry.getKey();
            String headerValue = entry.getValue();
            stringJoiner.add(httpHeader + ": " + headerValue + " ");
        }
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
