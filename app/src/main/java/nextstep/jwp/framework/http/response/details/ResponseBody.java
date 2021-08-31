package nextstep.jwp.framework.http.response.details;

public class ResponseBody {

    private final String content;

    public ResponseBody(final String content) {
        this.content = content;
    }

    public static ResponseBody of(final String content) {
        return new ResponseBody(content);
    }

    public String getContent() {
        return content;
    }
}
