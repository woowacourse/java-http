package nextstep.jwp.http.message.response;

import nextstep.jwp.http.common.MediaType;
import nextstep.jwp.http.message.HeaderFields;
import nextstep.jwp.http.message.MessageHeader;

import java.util.Objects;
import java.util.Optional;

public class ResponseHeader implements MessageHeader {

    private final HeaderFields headerFields;

    public ResponseHeader() {
        this(new HeaderFields());
    }

    public ResponseHeader(HeaderFields headerFields) {
        this.headerFields = headerFields;
    }

    public String asString() {
        return headerFields.asString();
    }

    public void put(String key, String value) {
        headerFields.put(key, value);
    }

    public void putLocation(String uri) {
        headerFields.putLocation(uri);
    }

    public void putContentType(MediaType contentType) {
        headerFields.putContentType(contentType);
    }

    public void putContentLength(int contentLength) {
        headerFields.putContentLength(contentLength);
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
