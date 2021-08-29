package nextstep.jwp.model.httpMessage.request;

import nextstep.jwp.model.httpMessage.ContentType;
import nextstep.jwp.model.httpMessage.HttpHeaderType;
import nextstep.jwp.model.httpMessage.HttpStatus;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

import static nextstep.jwp.model.httpMessage.HttpHeaderType.CONTENT_LENGTH;
import static nextstep.jwp.model.httpMessage.HttpHeaderType.CONTENT_TYPE;

public class RequestHeader {
    private final Map<String, String> headers = new LinkedHashMap<>();
    private HttpStatus protocol;

    public void add(String name, String value) {
        headers.put(name, value);
    }

    public void add(HttpHeaderType name, String value) {
        headers.put(name.value(), value);
    }

    public void addProtocol(HttpStatus protocol) {
        this.protocol = protocol;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getAllHeaders() {
        StringJoiner stringJoiner = new StringJoiner("\r\n");
        stringJoiner.add(protocol.toString() + " ");
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

    public String getProtocol(HttpStatus protocol) {
        return protocol.toString();
    }

    public void setContentLength(int length) {
        headers.put(CONTENT_LENGTH.value(), String.valueOf(length));
    }

    public int getContentLength() {
        return Integer.parseInt(headers.get(CONTENT_LENGTH.value()));
    }

    public boolean containsKey(HttpHeaderType type) {
        return headers.containsKey(type.value());
    }

    public String getContentType() {
        ContentType contentType = ContentType.getType(headers.get(CONTENT_TYPE.value()))
                .orElseThrow(() -> new IllegalArgumentException("Content Type header를 찾을 수 없습니다."));
        return contentType.value();
    }
}
