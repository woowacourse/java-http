package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;

public class Http11ResponseBody {

    private final String body;

    private Http11ResponseBody(String body) {
        this.body = body;
    }

    public static Http11ResponseBody of(String staticResource) {
        return new Http11ResponseBody(staticResource);
    }

    public int getContentLength() {
        return body.getBytes(StandardCharsets.UTF_8).length;
    }

    @Override
    public String toString() {
        return body;
    }
}
