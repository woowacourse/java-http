package nextstep.jwp.handler;

import nextstep.jwp.controller.ViewController;
import org.apache.catalina.servlet.handler.AbstractRequestHandler;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class StaticResourceRequestHandler extends AbstractRequestHandler {

    public StaticResourceRequestHandler() {
        super("/*");
    }

    @Override
    protected Response doGet(final Request request) {
        return ViewController.resource(request);
    }

}
