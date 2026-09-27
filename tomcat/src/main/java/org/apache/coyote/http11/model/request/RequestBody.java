package org.apache.coyote.http11.model.request;

import java.io.BufferedReader;
import java.io.IOException;

public record RequestBody(
        String value
) {

    public static RequestBody of(int contentLength, BufferedReader reader) throws IOException {
        int readLength = 0;
        char[] buffer = new char[contentLength];
        while (readLength < contentLength) {
            int nowReadLength = reader.read(buffer, readLength, contentLength - readLength);
            if (nowReadLength == -1) {
                throw new IOException("잘못된 요청");
            }
            readLength += nowReadLength;
        }
        return new RequestBody(new String(buffer));
    }
}
