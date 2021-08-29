package nextstep.jwp.http.message.request;

import nextstep.jwp.http.common.HttpPath;
import nextstep.jwp.http.message.HttpMessage;
import nextstep.jwp.http.message.MessageBody;
import nextstep.jwp.utils.StringUtils;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;

public class HttpRequestMessage implements HttpMessage {

    private static final String MESSAGE_SEPARATOR = "\r\n\r\n";
    private static final String LINE_SEPARATOR = "\r\n";
    private static final int HAS_BODY_COUNT = 2;

    private final RequestHeader requestHeader;
    private final MessageBody responseBody;

    public HttpRequestMessage(RequestHeader requestHeader, MessageBody responseBody) {
        this.requestHeader = requestHeader;
        this.responseBody = responseBody;
    }

    public HttpRequestMessage(String requestMessage) {
        this(
                RequestHeader.from(parseHeaderString(requestMessage)),
                new MessageBody(parseBodyString(requestMessage))
        );
    }

    private static String parseHeaderString(String requestMessage) {
        return StringUtils.splitTwoPiecesWithSeparator(requestMessage, MESSAGE_SEPARATOR).get(0);
    }

    private static String parseBodyString(String requestMessage) {
        if (!hasBody(requestMessage)) {
            return "";
        }
        return StringUtils.splitTwoPiecesWithSeparator(requestMessage, MESSAGE_SEPARATOR).get(1);
    }

    private static boolean hasBody(String requestMessage) {
        List<String> headerBodies = StringUtils.splitWithSeparator(requestMessage, MESSAGE_SEPARATOR);
        return headerBodies.size() >= HAS_BODY_COUNT;
    }

    public void changeRequestUri(String requestUri) {
        requestHeader.changeRequestUri(requestUri);
    }

    public String requestUri() {
        return requestHeader.requestUri();
    }

    public HttpPath requestPath() {
        return requestHeader.requestPath();
    }

    @Override
    public RequestHeader getHeader() {
        return this.requestHeader;
    }

    @Override
    public MessageBody getBody() {
        return this.responseBody;
    }

    @Override
    public byte[] toBytes() {
        byte[] headerBytes = requestHeader.toBytes();
        byte[] separatorBytes = LINE_SEPARATOR.getBytes();
        byte[] bodyBytes = responseBody.getBytes();

        return ByteBuffer.allocate(headerBytes.length + separatorBytes.length + bodyBytes.length)
                .put(headerBytes)
                .put(separatorBytes)
                .put(bodyBytes)
                .array();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpRequestMessage that = (HttpRequestMessage) o;
        return Objects.equals(requestHeader, that.requestHeader) && Objects.equals(responseBody, that.responseBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestHeader, responseBody);
    }

    @Override
    public String toString() {
        return new String(toBytes());
    }
}
