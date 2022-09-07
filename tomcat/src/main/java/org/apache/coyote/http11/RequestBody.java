package org.apache.coyote.http11;

public class RequestBody {

    private final String body;

    private RequestBody(String body) {
        this.body = body;
    }

    public static RequestBody from(char[] body) {
        return new RequestBody(new String(body));
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "RequestBody{" +
                "body='" + body + '\'' +
                '}';
    }
}
