package nextstep.jwp.http.response;

import java.util.Objects;

public class ResponseBody {

    private static final ResponseBody EMPTY = new ResponseBody(null);
    private static final String NEW_LINE = System.getProperty("line.separator");

    private final String value;

    public ResponseBody(String value) {
        this.value = value;
    }

    public static ResponseBody empty() {
        return EMPTY;
    }

    public boolean isEmpty() {
        return Objects.isNull(value);
    }

    @Override
    public String toString() {
        return NEW_LINE + value;
    }
}
