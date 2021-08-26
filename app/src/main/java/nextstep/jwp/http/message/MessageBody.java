package nextstep.jwp.http.message;

import java.util.Arrays;

public class MessageBody {

    private final byte[] bytes;

    public MessageBody(String bodyString) {
        this(bodyString.getBytes());
    }

    public MessageBody(byte[] bytes) {
        this.bytes = bytes;
    }

    public int contentLength() {
        return bytes.length;
    }

    public byte[] getBytes() {
        return bytes;
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
