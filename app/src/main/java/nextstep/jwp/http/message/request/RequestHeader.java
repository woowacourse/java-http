package nextstep.jwp.http.message.request;

import nextstep.jwp.http.message.HeaderFields;
import nextstep.jwp.http.message.MessageHeader;

import java.util.Objects;

public class RequestHeader implements MessageHeader {

    private final HeaderFields headerFields;

    public RequestHeader(String headerFields) {
        this(HeaderFields.from(headerFields));
    }

    public RequestHeader(HeaderFields headerFields) {
        this.headerFields = headerFields;
    }

    public byte[] toBytes() {
        return convertToString().getBytes();
    }

    public String convertToString() {
        return headerFields.asString();
    }

    public int takeContentLength() {
        String contentLength = headerFields.take("Content-Length").orElse("0");
        return Integer.parseInt(contentLength);
    }

    @Override
    public HeaderFields getHeaderFields() {
        return this.headerFields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestHeader that = (RequestHeader) o;
        return Objects.equals(getHeaderFields(), that.getHeaderFields());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHeaderFields());
    }
}
