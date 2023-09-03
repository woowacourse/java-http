package nextstep.jwp.http;

public class HttpBody {

    private final String httpBody;

    private HttpBody(String httpBody) {
        this.httpBody = httpBody;
    }

    public static HttpBody from(String httpBody) {
        return new HttpBody(httpBody);
    }

    public String getHttpBody() {
        return httpBody;
    }

    public int getBytesLength(){
        return httpBody.getBytes().length;
    }
}
