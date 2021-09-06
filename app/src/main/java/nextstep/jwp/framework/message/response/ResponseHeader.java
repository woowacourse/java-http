package nextstep.jwp.framework.message.response;

import nextstep.jwp.framework.common.MediaType;
import nextstep.jwp.framework.message.HeaderFields;
import nextstep.jwp.framework.message.MessageHeader;

import java.util.Objects;
import java.util.Optional;

public class ResponseHeader implements MessageHeader {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String LOCATION = "Location";

    private final HeaderFields headerFields;

    public ResponseHeader(HeaderFields headerFields) {
        this.headerFields = headerFields;
    }

    public ResponseHeader() {
        this(HeaderFields.empty());
    }

    public String asString() {
        return headerFields.asString();
    }

    public void put(String key, String value) {
        headerFields.put(key, value);
    }

    public void putLocation(String uri) {
        put(LOCATION, uri);
    }

    public void putContentType(MediaType contentType) {
        put(CONTENT_TYPE, contentType.getValue());
    }

    public void putContentLength(int contentLength) {
        put(CONTENT_LENGTH, String.valueOf(contentLength));
    }

    public void putSetCookie(String name, String value) {
        put(SET_COOKIE, String.format("%s=%s", name, value));
    }

    public Optional<String> take(String key) {
        return headerFields.take(key);
    }

    @Override
    public byte[] toBytes() {
        return asString().getBytes();
    }

    @Override
    public HeaderFields getHeaderFields() {
        return this.headerFields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResponseHeader that = (ResponseHeader) o;
        return Objects.equals(getHeaderFields(), that.getHeaderFields());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHeaderFields());
    }
}
