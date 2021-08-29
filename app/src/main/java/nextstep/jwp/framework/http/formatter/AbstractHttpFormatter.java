package nextstep.jwp.framework.http.formatter;

import java.util.Objects;

import nextstep.jwp.framework.http.HttpMessage;

public abstract class AbstractHttpFormatter implements HttpFormatter {

    protected final HttpMessage httpMessage;

    protected AbstractHttpFormatter(HttpMessage httpMessage) {
        this.httpMessage = Objects.requireNonNull(httpMessage);
    }

    public boolean canRead() {
        return true;
    }
}
