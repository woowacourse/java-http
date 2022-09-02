package nextstep.jwp.http.response;

public class ResponseBody {

    private static final ResponseBody EMPTY_BODY = new ResponseBody(null);

    private final String value;

    public ResponseBody(final String value) {
        this.value = value;
    }

    public static ResponseBody empty() {
        return EMPTY_BODY;
    }

    public String getValue() {
        return value;
    }
}
