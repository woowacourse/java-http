package nextstep.jwp.framework.http.formatter;

import java.util.Objects;

import nextstep.jwp.framework.http.HttpMessage;

public abstract class AbstractLineFormatter implements LineFormatter {

    protected final HttpMessage httpMessage;

    public AbstractLineFormatter(HttpMessage httpMessage) {
        this.httpMessage = Objects.requireNonNull(httpMessage);
    }

    public boolean canRead() {
        return true;
    }
}
