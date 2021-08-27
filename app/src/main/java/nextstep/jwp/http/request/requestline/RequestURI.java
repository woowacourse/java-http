package nextstep.jwp.http.request.requestline;

import java.util.Objects;
import nextstep.jwp.exception.InvalidRequestUriException;

public class RequestURI {

    private final String value;

    // TODO: '*'이 들어올 경우 모든 경로를 처리한다.
    public RequestURI(String value) {
        this.value = value;
        validateNull(this.value);
    }

    private void validateNull(String value) {
        if (Objects.isNull(value)) {
            throw new InvalidRequestUriException();
        }
    }
}
