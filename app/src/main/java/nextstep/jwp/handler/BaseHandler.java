package nextstep.jwp.handler;

import nextstep.jwp.model.Request;

public class BaseHandler extends AbstractHandler {

    @Override
    public String message(Request request) {
        final String responseBody = "Hello world!";
        return staticFileMessage(HTML, responseBody);
    }
}
