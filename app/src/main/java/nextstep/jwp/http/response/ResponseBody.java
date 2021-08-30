package nextstep.jwp.http.response;

public class ResponseBody {

    private final String body;

    public ResponseBody(byte[] bytes) {
        this(new String(bytes));
    }

    public ResponseBody(String body) {
        this.body = body;
    }

    public int getLength() {
        return getBytes().length;
    }

    public byte[] getBytes() {
        return body.getBytes();
    }

    public String getBody() {
        return body;
    }
}
