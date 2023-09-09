package org.apache.coyote.common;

import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MessageBody {

    private static final String EMPTY = "";

    private final String body;
    private final int bodyLength;

    private MessageBody(final String body) {
        this.body = body;
        this.bodyLength = body.getBytes(UTF_8).length;
    }

    public static MessageBody empty() {
        return new MessageBody(EMPTY);
    }

    public static MessageBody from(final char[] chars) {
        return new MessageBody(new String(chars));
    }

    public static MessageBody from(final String body) {
        return new MessageBody(body);
    }

    public String body() {
        return body;
    }

    public int bodyLength() {
        return bodyLength;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final MessageBody that = (MessageBody) o;
        return Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }

    @Override
    public String toString() {
        return "MessageBody{" +
               "body='" + body + '\'' +
               '}';
    }
}
