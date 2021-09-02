package nextstep.jwp.model.response;

public class ResponseBody {

    private final String body;

    public ResponseBody(String body) {
        this.body = body;
    }

    public String toMessage() {
        return body;
    }
}
