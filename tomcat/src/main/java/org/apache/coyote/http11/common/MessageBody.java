package org.apache.coyote.http11.common;

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

    public String getContent() {
        if (content == null) {
            return "";
        }
        return content;
    }
}
