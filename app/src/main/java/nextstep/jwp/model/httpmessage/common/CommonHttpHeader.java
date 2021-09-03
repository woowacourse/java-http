package nextstep.jwp.model.httpmessage.common;

import java.util.LinkedHashMap;
import java.util.Map;

import static nextstep.jwp.model.httpmessage.common.HttpHeaderType.CONTENT_LENGTH;
import static nextstep.jwp.model.httpmessage.common.HttpHeaderType.CONTENT_TYPE;

public abstract class CommonHttpHeader implements HttpHeader {

    public static final String DELIMITER = "\r\n";

    private final Map<String, String> commonHeaders = new LinkedHashMap<>();

    public boolean containsKey(HttpHeaderType type) {
        return commonHeaders.containsKey(type.value());
    }

    public boolean commonHeaderContains(String type) {
        return HttpHeaderType.contains(type);
    }

    public void add(HttpHeaderType type, String value) {
        commonHeaders.put(type.value(), value);
    }

    public Map<String, String> getCommonHeaders() {
        return commonHeaders;
    }

    @Override
    public String getHeader(String type) {
        return commonHeaders.get(HttpHeaderType.of(type).value());
    }

    @Override
    public void addHeader(String type, String value) {
        commonHeaders.put(HttpHeaderType.of(type).value(), value);
    }

    @Override
    public String getContentType() {
        return commonHeaders.get(CONTENT_TYPE.value());
    }

    @Override
    public void setContentType(String contentType) {
        commonHeaders.put(CONTENT_TYPE.value(), contentType);
    }

    @Override
    public int getContentLength() {
        return Integer.parseInt(commonHeaders.get(CONTENT_LENGTH.value()));
    }

    @Override
    public void setContentLength(int contentLength) {
        commonHeaders.put(CONTENT_LENGTH.value(), String.valueOf(contentLength));
    }
}
