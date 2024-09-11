package org.apache.coyote.http11.request;

public record RequestBody(String body) {

    public RequestBody {
        if (body == null) {
            throw new IllegalArgumentException("요청 본문은 null일 수 없습니다.");
        }
    }

    public static RequestBody empty() {
        return new RequestBody("");
    }

    public boolean isEmpty() {
        return body.isEmpty();
    }

    @Override
    public String toString() {
        return "RequestBody{" +
                "body='" + body + '\'' +
                '}';
    }
}
