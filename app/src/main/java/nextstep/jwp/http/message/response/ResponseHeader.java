package nextstep.jwp.http.message.response;

import nextstep.jwp.http.message.HeaderFields;
import nextstep.jwp.http.message.MessageHeader;

import java.util.Objects;

public class ResponseHeader implements MessageHeader {

    private final HeaderFields headerFields;

    public ResponseHeader(HeaderFields headerFields) {
        this.headerFields = headerFields;
    }

    public String asString() {
        return headerFields.asString();
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
