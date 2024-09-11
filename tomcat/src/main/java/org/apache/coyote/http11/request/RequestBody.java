package org.apache.coyote.http11.request;

public class RequestBody {

    private static final String EMPTY_BODY = "";

    private final String body;

    public RequestBody(String body) {
        this.body = body;
    }

    public static RequestBody empty() {
        return new RequestBody(EMPTY_BODY);
    }

    public boolean isEmpty() {
        return body.isEmpty();
    }

    public String getBodyValue() {
        if (body.isEmpty()) {
            throw new IllegalArgumentException("Body가 존재하지 않습니다.");
        }
        return body;
    }
}
