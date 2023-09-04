package org.apache.coyote.httprequest;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestBody {

    private final String contents;

    private RequestBody(final String contents) {
        this.contents = contents;
    }

    public static RequestBody from(final BufferedReader bufferedReader) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();
        String line;
        if (!bufferedReader.ready()) {
            return new RequestBody("");
        }
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line).append("\r\n");
        }
        return new RequestBody(stringBuilder.toString());
    }
}
