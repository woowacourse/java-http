package nextstep.jwp.model.web.response;

public class ResponseBody {

    private final String body;

    public ResponseBody(String body) {
        this.body = body;
    }

    public byte[] bodyBytes() {
        return body.getBytes();
    }
}
