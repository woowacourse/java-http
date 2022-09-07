package org.apache.coyote.http11.response;

import java.io.IOException;
import nextstep.jwp.utils.FileReader;

public class Body {

    private static final String DEFAULT_BODY = "Hello world!";
    private static final String EMPTY_BODY = "";

    private final String body;

    private Body(String body) {
        this.body = body;
    }

    public Body() {
        this.body = EMPTY_BODY;
    }

    public static Body from(String url) throws IOException {
        return new Body(createBody(url));
    }

    private static String createBody(String url) throws IOException {
        return FileReader.readByPath(url)
                .orElse(DEFAULT_BODY);
    }

    public String getBody() {
        return body;
    }
}
