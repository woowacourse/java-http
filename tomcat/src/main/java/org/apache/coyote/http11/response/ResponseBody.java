package org.apache.coyote.http11.response;

import java.io.IOException;
import java.util.Objects;

public class ResponseBody {
    private final String body;

    public ResponseBody(String body) throws IOException {
        this.body = body;
    }

    public int getLength() {
        if (Objects.isNull(body) || body.isEmpty()) {
            return 0;
        }
        return body.getBytes().length;
    }

    public String getBody() {
        return body;
    }
}
