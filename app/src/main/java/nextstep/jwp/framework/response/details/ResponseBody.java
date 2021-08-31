package nextstep.jwp.framework.response.details;

public class ResponseBody {
    private final String content;

    public ResponseBody(String content) {
        this.content = content;
    }

    public static ResponseBody of(String content) {
        return new ResponseBody(content);
    }

    public String getContent() {
        return content;
    }
}
