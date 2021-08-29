package nextstep.jwp.web.http.response;

public class ResponseBody {

    private String body = "";

    public ResponseBody() {
    }

    public ResponseBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
