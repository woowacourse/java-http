package nextstep.jwp.framework.http.formatter;

import nextstep.jwp.framework.http.HttpMessage;

public class BodyFormatter extends AbstractHttpFormatter {

    public BodyFormatter(HttpMessage httpMessage) {
        super(httpMessage);
    }

    @Override
    public String transform() {
        return httpMessage.getBody();
    }

    @Override
    public HttpFormatter convertNextFormatter() {
        return new EndLineFormatter(httpMessage);
    }
}
