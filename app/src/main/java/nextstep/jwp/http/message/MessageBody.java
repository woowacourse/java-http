package nextstep.jwp.http.message;

import java.util.Arrays;

public class MessageBody {

    private final byte[] bytes;

    public MessageBody() {
        this(new byte[0]);
    }

    public MessageBody(byte[] bytes) {
        this.bytes = bytes;
    }

    public MessageBody(String bodyString) {
        this(bodyString.getBytes());
    }

    public String contentLength() {
        return String.valueOf(bytes.length);
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
