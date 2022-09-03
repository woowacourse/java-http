package nextstep.jwp.http.response;

public class ResponseBody {

    private final String value;

    public ResponseBody(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
