package nextstep.jwp.http.message.response;

import nextstep.jwp.http.common.HttpStatusCode;
import nextstep.jwp.http.message.HeaderFields;
import nextstep.jwp.http.message.MessageHeader;

import java.util.Objects;

public class ResponseHeader implements MessageHeader {

    private static final String BLANK = " ";
    private static final String LINE_SEPARATOR = "\r\n";

    private final String httpVersion;
    private HttpStatusCode httpStatusCode;
    private final HeaderFields headerFields;

    public ResponseHeader() {
        this("HTTP/1.1", null, new HeaderFields());
    }

    public ResponseHeader(String httpVersion, HttpStatusCode httpStatusCode, HeaderFields headerFields) {
        this.httpVersion = httpVersion;
        this.httpStatusCode = httpStatusCode;
        this.headerFields = headerFields;
    }

    public byte[] toBytes() {
        return convertToString().getBytes();
    }

    public String convertToString() {
        return statusLine() + headerFields.asString();
    }

    private String statusLine() {
        String statusCode = String.valueOf(httpStatusCode.getCode());
        String description = httpStatusCode.getDescription();
        return String.join(BLANK,
                httpVersion,
                statusCode,
                description,
                LINE_SEPARATOR);
    }

    public void putHeader(String key, String value) {
        headerFields.put(key, value);
    }

    public void setHttpStatusCode(HttpStatusCode httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    @Override
    public HeaderFields getHeaderFields() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResponseHeader that = (ResponseHeader) o;
        return Objects.equals(httpVersion, that.httpVersion) && httpStatusCode == that.httpStatusCode && Objects.equals(getHeaderFields(), that.getHeaderFields());
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpVersion, httpStatusCode, getHeaderFields());
    }
}
