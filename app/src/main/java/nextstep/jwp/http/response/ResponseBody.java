package nextstep.jwp.http.response;

import java.util.Objects;
import nextstep.jwp.exception.NoResponseBodyException;

public class ResponseBody {

    private static final ResponseBody EMPTY = new ResponseBody(null);

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
        if (isEmpty()) {
            throw new NoResponseBodyException();
        }

        return value;
    }
}
