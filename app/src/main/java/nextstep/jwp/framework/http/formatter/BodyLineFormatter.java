package nextstep.jwp.framework.http.formatter;

import nextstep.jwp.framework.http.HttpResponse;

public class BodyLineFormatter extends AbstractLineFormatter {

    public BodyLineFormatter(HttpResponse httpResponse) {
        super(httpResponse);
    }

    @Override
    public String transform() {
        return httpResponse.getResponseBody();
    }

    @Override
    public LineFormatter convertNextFormatter() {
        return new EndLineFormatter(httpResponse);
    }
}
