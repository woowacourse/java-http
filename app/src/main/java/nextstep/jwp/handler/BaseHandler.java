package nextstep.jwp.handler;

import nextstep.jwp.model.Request;

public class BaseHandler extends AbstractHandler {

    public static final String BASE_RESPONSE_BODY = "Hello world!";

    @Override
    public String message(Request request) {
        return staticFileMessage(HTML, BASE_RESPONSE_BODY);
    }
}
