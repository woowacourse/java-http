package org.apache.coyote.response;

import org.apache.coyote.common.MessageBody;

import java.util.Objects;

public class ResponseBody {

    private MessageBody messageBody;

    private ResponseBody(final MessageBody messageBody) {
        this.messageBody = messageBody;
    }

    public static ResponseBody empty() {
        return new ResponseBody(MessageBody.empty());
    }

    public static ResponseBody from(final String body) {
        return new ResponseBody(MessageBody.from(body));
    }

    public String body() {
        return messageBody.body();
    }

    public int length() {
        return messageBody.bodyLength();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ResponseBody that = (ResponseBody) o;
        return Objects.equals(messageBody, that.messageBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageBody);
    }

    @Override
    public String toString() {
        return "ResponseBody{" +
               "messageBody=" + messageBody +
               '}';
    }
}
