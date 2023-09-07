package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.common.Method.GET;
import static org.apache.coyote.http11.common.Method.POST;

import java.util.List;
import nextstep.jwp.controller.UserController;
import nextstep.jwp.controller.ViewController;
import org.apache.coyote.http11.common.Method;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class RegisterHandler extends AbstractHandler {

    private static final List<Method> allowMethods = List.of(GET, POST);

    @Override
    protected Response doGet(final Request request) {
        return ViewController.register(request);
    }

    @Override
    protected Response doPost(final Request request) {
        return UserController.register(request);
    }

    @Override
    public boolean canHandle(final Request request) {
        return "/register".equals(request.getPath()) && allowMethods.contains(request.getMethod());
    }
}
