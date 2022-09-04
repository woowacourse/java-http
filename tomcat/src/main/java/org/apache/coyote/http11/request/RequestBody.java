package org.apache.coyote.http11.request;

import java.util.List;

public class RequestBody {

    private final String body;

    private RequestBody(String body) {
        this.body = body;
    }

    public static RequestBody parse(List<String> lines) {
        return new RequestBody(String.join("\n", lines));
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "RequestBody{\n" +
                "body='" + body + '\'' +
                "\n}";
    }
}
