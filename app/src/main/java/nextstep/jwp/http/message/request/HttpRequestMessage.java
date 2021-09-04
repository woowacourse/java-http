package nextstep.jwp.http.message.request;

import nextstep.jwp.http.message.HttpMessage;
import nextstep.jwp.http.message.MessageBody;
import nextstep.jwp.http.message.MessageHeader;
import nextstep.jwp.http.message.StartLine;
import nextstep.jwp.utils.BytesUtils;

import java.util.Objects;

public class HttpRequestMessage implements HttpMessage {

    private static final String NEW_LINE = "\r\n";

    private final StartLine requestLine;
    private final MessageHeader requestHeader;
    private final MessageBody requestBody;

    public HttpRequestMessage(StartLine requestLine, MessageHeader requestHeader) {
        this(requestLine, requestHeader, MessageBody.empty());
    }

    public HttpRequestMessage(StartLine requestLine, MessageHeader requestHeader, MessageBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    @Override
    public StartLine getStartLine() {
        return requestLine;
    }

    @Override
    public MessageHeader getHeader() {
        return this.requestHeader;
    }

    @Override
    public MessageBody getBody() {
        return this.requestBody;
    }

    @Override
    public byte[] toBytes() {
        return BytesUtils.concat(
                requestLine.toBytes(),
                requestHeader.toBytes(),
                NEW_LINE.getBytes(),
                requestBody.getBytes()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpRequestMessage that = (HttpRequestMessage) o;
        return Objects.equals(requestLine, that.requestLine)
                && Objects.equals(requestHeader, that.requestHeader)
                && Objects.equals(requestBody, that.requestBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestLine, requestHeader, requestBody);
    }

    @Override
    public String toString() {
        return new String(toBytes());
    }
}
