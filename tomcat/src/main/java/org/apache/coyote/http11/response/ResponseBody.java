package org.apache.coyote.http11.response;

import java.io.IOException;
import nextstep.jwp.utils.FileReader;

public class ResponseBody {

    private static final String DEFAULT_BODY = "Hello world!";
    private static final String EMPTY_BODY = "";

    private final String body;

    private ResponseBody(String body) {
        this.body = body;
    }

    public ResponseBody() {
        this.body = EMPTY_BODY;
    }

    public static ResponseBody from(String url) throws IOException {
        return new ResponseBody(createBody(url));
    }

    private static String createBody(String url) throws IOException {
        return FileReader.readByPath(url)
                .orElse(DEFAULT_BODY);
    }

    public String getBody() {
        return body;
    }
}
