package nextstep.jwp.http.response;

public class ResponseBody {

    private final String content;

    public ResponseBody(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }
}
