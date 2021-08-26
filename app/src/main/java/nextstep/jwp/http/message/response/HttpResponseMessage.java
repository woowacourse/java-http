package nextstep.jwp.http.message.response;

import nextstep.jwp.http.HttpStatusCode;
import nextstep.jwp.http.message.HeaderFields;
import nextstep.jwp.http.message.HttpMessage;
import nextstep.jwp.http.message.MessageBody;

import java.nio.ByteBuffer;

public class HttpResponseMessage implements HttpMessage {

    private static final String LINE_SEPARATOR = "\r\n";

    private final ResponseHeader responseHeader;
    private MessageBody responseBody;

    public HttpResponseMessage() {
        this(new ResponseHeader(), new MessageBody());
    }

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

    public void putHeader(String key, String value) {
        this.responseHeader.putHeader(key, value);
    }

    public void setStatusCode(HttpStatusCode httpStatusCode) {
        responseHeader.setHttpStatusCode(httpStatusCode);
    }

    public void setMessageBody(MessageBody messageBody) {
        this.responseBody = messageBody;
    }

    @Override
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

    @Override
    public ResponseHeader getHeader() {
        return responseHeader;
    }

    @Override
    public MessageBody getBody() {
        return this.responseBody;
    }

    @Override
    public String toString() {
        return new String(toBytes());
    }
}
