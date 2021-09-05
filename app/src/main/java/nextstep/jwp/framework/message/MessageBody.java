package nextstep.jwp.framework.message;

import nextstep.jwp.framework.message.request.FormData;

import java.util.Arrays;

public class MessageBody {

    private static final MessageBody EMPTY_BODY = new MessageBody(new byte[0]);

    private final byte[] bytes;

    private MessageBody(byte[] bytes) {
        this.bytes = bytes;
    }

    public static MessageBody empty() {
        return EMPTY_BODY;
    }

    public static MessageBody from(byte[] bytes) {
        if (bytes.length == 0) {
            return EMPTY_BODY;
        }
        return new MessageBody(bytes);
    }

    public static MessageBody from(String bodyString) {
        if (bodyString.isEmpty()) {
            return EMPTY_BODY;
        }
        return new MessageBody(bodyString.getBytes());
    }

    public boolean isEmpty() {
        return this == EMPTY_BODY;
    }

    public FormData toFormData() {
        return FormData.from(this);
    }

    public int bodyLength() {
        return bytes.length;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String asString() {
        return new String(bytes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageBody that = (MessageBody) o;
        return Arrays.equals(getBytes(), that.getBytes());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getBytes());
    }
}
