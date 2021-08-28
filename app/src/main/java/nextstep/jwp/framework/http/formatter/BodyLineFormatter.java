package nextstep.jwp.framework.http.formatter;

import nextstep.jwp.framework.http.HttpMessage;

public class BodyLineFormatter extends AbstractLineFormatter {

    public BodyLineFormatter(HttpMessage httpMessage) {
        super(httpMessage);
    }

    @Override
    public String transform() {
        return httpMessage.getBody();
    }

    @Override
    public LineFormatter convertNextFormatter() {
        return new EndLineFormatter(httpMessage);
    }
}
