package nextstep.jwp.framework.message.request;

import nextstep.jwp.framework.message.HeaderFields;
import nextstep.jwp.framework.message.MessageHeader;

import java.util.Objects;

public class RequestHeader implements MessageHeader {

    private static final String COOKIE = "Cookie";
    private static final String CONTENT_LENGTH = "Content-Length";

    private final HeaderFields headerFields;

    private RequestHeader(HeaderFields headerFields) {
        this.headerFields = headerFields;
    }

    public static RequestHeader from(HeaderFields headerFields) {
        return new RequestHeader(headerFields);
    }

    public static RequestHeader from(String headerMessage) {
        return from(HeaderFields.from(headerMessage));
    }

    public int takeContentLength() {
        String contentLength = headerFields.take(CONTENT_LENGTH).orElse("0");
        return Integer.parseInt(contentLength);
    }

    public HttpCookies extractHttpCookies() {
        return headerFields.take(COOKIE)
                .map(HttpCookies::from)
                .orElseGet(HttpCookies::empty);
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
        RequestHeader that = (RequestHeader) o;
        return Objects.equals(getHeaderFields(), that.getHeaderFields());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHeaderFields());
    }
}
