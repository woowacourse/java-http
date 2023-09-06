package nextstep.jwp.http;

public class HttpBody {

    private final String message;

    private HttpBody(String message) {
        this.message = message;
    }

    public static HttpBody from(String httpBody) {
        if (httpBody == null) {
            throw new IllegalArgumentException("HttpBody is Null");
        }

        return new HttpBody(httpBody);
    }

    public static HttpBody createEmptyBody() {
        return new HttpBody("");
    }

    public int getBytesLength() {
        return message.getBytes().length;
    }

    public String getMessage() {
        return message;
    }

}
