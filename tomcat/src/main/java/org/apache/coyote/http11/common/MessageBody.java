package org.apache.coyote.http11.common;

import java.io.BufferedReader;
import java.io.IOException;

public class MessageBody {

    private final String value;

    private MessageBody(final String value) {
        this.value = value;
    }

    public static MessageBody from(final BufferedReader br) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (br.ready()) {
            sb.append(br.readLine())
                    .append("\r\n");
        }
        return new MessageBody(sb.toString());
    }

    public static MessageBody from(final String messageBody) {
        return new MessageBody(messageBody);
    }

    public int getBodyLength() {
        return value.getBytes().length;
    }

    public String getValue() {
        return value;
    }
}
