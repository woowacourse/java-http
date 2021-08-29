package nextstep.jwp.model.httpMessage;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

import static nextstep.jwp.model.httpMessage.HttpHeaderType.CONTENT_LENGTH;

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
        stringJoiner.add(protocol.getProtocol() + " ");
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

    public String getProtocol(HttpProtocol protocol) {
        return protocol.getProtocol();
    }

    public void setContentLength(int length) {
        headers.put(CONTENT_LENGTH.value(), String.valueOf(length));
    }
}
