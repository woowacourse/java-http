package nextstep.jwp.http.response;

public class ResponseBody {

    private String content = "";

    public ResponseBody() {
    }

    public ResponseBody(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }
}
