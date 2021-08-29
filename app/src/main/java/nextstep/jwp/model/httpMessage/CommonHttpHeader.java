package nextstep.jwp.model.httpMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static nextstep.jwp.model.httpMessage.HttpHeaderType.CONTENT_LENGTH;
import static nextstep.jwp.model.httpMessage.HttpHeaderType.CONTENT_TYPE;

public abstract class CommonHttpHeader implements HttpHeader {

    public static final String DELIMITER = "\r\n";

    private static final Logger LOG = LoggerFactory.getLogger(CommonHttpHeader.class);

    private final Map<HttpHeaderType, String> commonHeaders = new LinkedHashMap<>();

    public boolean containsKey(HttpHeaderType type) {
        return commonHeaders.containsKey(type);
    }

    public boolean commonHeaderContains(String type) {
        return HttpHeaderType.contains(type);
    }

    public void add(HttpHeaderType type, String value) {
        commonHeaders.put(type, value);
    }

    public Map<HttpHeaderType, String> getCommonHeaders() {
        return commonHeaders;
    }

    @Override
    public String getHeader(String type) {
        return commonHeaders.get(HttpHeaderType.of(type));
    }

    @Override
    public void addHeader(String type, String value) {
        commonHeaders.put(HttpHeaderType.of(type), value);
    }

    @Override
    public void setContentType(String contentType) {
        commonHeaders.put(CONTENT_TYPE, contentType);
    }

    @Override
    public String getContentType() {
        return commonHeaders.get(CONTENT_TYPE);
    }

    @Override
    public void setContentLength(int contentLength) {
        commonHeaders.put(CONTENT_LENGTH, String.valueOf(contentLength));
        LOG.debug("Response header : content length: {}", contentLength);
    }

    @Override
    public int getContentLength() {
        return Integer.parseInt(commonHeaders.get(CONTENT_LENGTH));
    }
}
