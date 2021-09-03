package nextstep.jwp.framework.http.formatter;

import nextstep.jwp.framework.http.HttpMessage;

public class BodyFormatter implements HttpFormatter {

    private final String body;

    public BodyFormatter(String body) {
        this.body = body;
    }

    @Override
    public String transform() {
        return body;
    }

    @Override
    public HttpFormatter convertNextFormatter(HttpMessage httpMessage) {
        return new EndLineFormatter();
    }
}
