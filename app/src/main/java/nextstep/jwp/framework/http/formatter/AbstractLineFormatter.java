package nextstep.jwp.framework.http.formatter;

import java.util.Objects;

import nextstep.jwp.framework.http.HttpResponse;

public abstract class AbstractLineFormatter implements LineFormatter {

    protected final HttpResponse httpResponse;

    public AbstractLineFormatter(HttpResponse httpResponse) {
        this.httpResponse = Objects.requireNonNull(httpResponse);
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

    public boolean canRead() {
        return true;
    }
}
