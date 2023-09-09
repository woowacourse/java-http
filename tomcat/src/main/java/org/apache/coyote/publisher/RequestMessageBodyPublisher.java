package org.apache.coyote.publisher;

import org.apache.coyote.common.MessageBody;

public class RequestMessageBodyPublisher {

    private final char[] chars;

    private RequestMessageBodyPublisher(final char[] chars) {
        this.chars = chars;
    }

    public static RequestMessageBodyPublisher read(final char[] chars) {
        return new RequestMessageBodyPublisher(chars);
    }

    public MessageBody toMessageBody() {
        return MessageBody.from(chars);
    }
}
