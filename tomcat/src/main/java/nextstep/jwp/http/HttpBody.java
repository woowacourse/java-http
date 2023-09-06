package nextstep.jwp.http;

public class HttpBody {

    private final String httpBody;

    private HttpBody(String httpBody) {
        this.httpBody = httpBody;
    }

    public static HttpBody from(String httpBody) {
        if (httpBody == null) {
            throw new IllegalArgumentException("HttpBody is Null");
        }

        return new HttpBody(httpBody);
    }

    public int getBytesLength() {
        return httpBody.getBytes().length;
    }

    public String getHttpBody() {
        return httpBody;
    }

}
