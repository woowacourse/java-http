package org.apache.coyote.http11.request;

public class MessageBody {
    private final String content;

    private MessageBody(String content) {
        this.content = content;
    }

    public static MessageBody empty() {
        return new MessageBody(null);
    }

    public static MessageBody create(final String input) {
        return new MessageBody(input);
    }
}
