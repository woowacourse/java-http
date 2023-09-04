package org.apache.coyote.httprequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestBody {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private final String contents;

    private RequestBody(final String contents) {
        this.contents = contents;
    }

    public static RequestBody from(final BufferedReader bufferedReader) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();
        log.debug("Request body: \n");
        String line;
        if (!bufferedReader.ready()) {
            return new RequestBody("");
        }
        while ((line = bufferedReader.readLine()) != null) {
            log.debug("\t" + line + "\n");
            stringBuilder.append(line).append("\r\n");
        }
        return new RequestBody(stringBuilder.toString());
    }
}
