package nextstep.jwp.http.message.response;

import nextstep.jwp.http.HttpStatusCode;
import nextstep.jwp.http.message.HeaderFields;
import nextstep.jwp.http.message.HttpMessage;
import nextstep.jwp.http.message.MessageBody;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Optional;

public class HttpResponseMessage implements HttpMessage {

    private static final String LINE_SEPARATOR = "\r\n";

    private final ResponseHeader responseHeader;
    private final MessageBody responseBody;

    public HttpResponseMessage(String httpVersion, HttpStatusCode httpStatusCode, HeaderFields headerFields) {
        this(httpVersion, httpStatusCode, headerFields, null);
    }

    public HttpResponseMessage(String httpVersion, HttpStatusCode httpStatusCode, HeaderFields headerFields, byte[] bytes) {
        this(new ResponseHeader(httpVersion, httpStatusCode, headerFields), new MessageBody(bytes));
    }

    private HttpResponseMessage(ResponseHeader responseHeader, MessageBody responseBody) {
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    public byte[] toBytes() {
        byte[] headerBytes = responseHeader.toBytes();
        byte[] separatorBytes = LINE_SEPARATOR.getBytes();
        byte[] bodyBytes = responseBody.getBytes();

        return ByteBuffer.allocate(headerBytes.length + separatorBytes.length + bodyBytes.length)
                .put(headerBytes)
                .put(separatorBytes)
                .put(bodyBytes)
                .array();
    }

    private int contentLength() {
        if (Objects.isNull(responseBody)) {
            return 0;
        }
        return responseBody.contentLength();
    }

    @Override
    public ResponseHeader getHeader() {
        return responseHeader;
    }

    @Override
    public Optional<MessageBody> getBody() {
        return Optional.ofNullable(responseBody);
    }
}
