package nextstep.jwp.http.entity;

public class HttpBody {
    private final String body;

    private HttpBody(String body) {
        this.body = body;
    }

    public static HttpBody of(String body) {
        return new HttpBody(body);
    }

    public static HttpBody empty() {
        return new HttpBody("");
    }

    public String body() {
        return body;
    }
}
