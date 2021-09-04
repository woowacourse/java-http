package nextstep.jwp.http.message.response;

import nextstep.jwp.http.common.HttpStatusCode;
import nextstep.jwp.http.message.HttpMessage;
import nextstep.jwp.http.message.MessageBody;
import nextstep.jwp.http.message.MessageHeader;
import nextstep.jwp.http.message.StartLine;
import nextstep.jwp.utils.BytesUtils;

import java.util.Objects;
import java.util.Optional;

public class HttpResponseMessage implements HttpMessage {

    private static final String NEW_LINE = "\r\n";

    private final StartLine statusLine;
    private final MessageHeader responseHeader;
    private final MessageBody responseBody;

    public HttpResponseMessage(StartLine statusLine, MessageHeader responseHeader, MessageBody responseBody) {
        this.statusLine = statusLine;
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    public HttpStatusCode statusCode() {
        return ((StatusLine) statusLine).getHttpStatusCode();
    }

    public Optional<String> takeHeaderValue(String key) {
        return ((ResponseHeader) responseHeader).take(key);
    }

    @Override
    public StartLine getStartLine() {
        return this.statusLine;
    }

    @Override
    public MessageHeader getHeader() {
        return this.responseHeader;
    }

    @Override
    public MessageBody getBody() {
        return this.responseBody;
    }

    @Override
    public byte[] toBytes() {
        return BytesUtils.concat(
                statusLine.toBytes(),
                responseHeader.toBytes(),
                NEW_LINE.getBytes(),
                responseBody.getBytes()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpResponseMessage that = (HttpResponseMessage) o;
        return Objects.equals(statusLine, that.statusLine)
                && Objects.equals(responseHeader, that.responseHeader)
                && Objects.equals(responseBody, that.responseBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusLine, responseHeader, responseBody);
    }

    @Override
    public String toString() {
        return new String(toBytes());
    }
}
