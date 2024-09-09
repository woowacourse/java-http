package org.apache.coyote.http11.request;

public class RequestBody {

    private final String body;

    public RequestBody(String body) {
        if (body == null) {
            throw new IllegalArgumentException("요청 본문은 null일 수 없습니다.");
        }
        this.body = body;
    }

    public static RequestBody empty() {
        return new RequestBody("");
    }

    public boolean isEmpty() {
        return body.isEmpty();
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
