package nextstep.jwp.controller;

import org.apache.coyote.Controller;
import org.apache.coyote.http11.message.request.Request;
import org.apache.coyote.http11.message.response.Response;

public class HelloController implements Controller {

    @Override
    public Response service(final Request request) {
        return Response.ofOk("Hello world!");
    }

    @Override
    public boolean canHandle(final Request request) {
        return request.isPath("/");
    }
}
