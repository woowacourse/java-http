package org.apache.coyote.http11.http.domain;

import java.io.BufferedReader;
import java.io.IOException;

public class MessageBody {

    private final String value;

    public MessageBody(final String value) {
        this.value = value;
    }

    public static MessageBody from(final BufferedReader bufferedReader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        while (bufferedReader.ready()) {
            stringBuilder.append(bufferedReader.readLine())
                    .append(HttpConstants.CRLF);
        }
        return new MessageBody(stringBuilder.toString());
    }

    public int length() {
        return value.getBytes().length;
    }

    public String getValue() {
        return value;
    }
}
