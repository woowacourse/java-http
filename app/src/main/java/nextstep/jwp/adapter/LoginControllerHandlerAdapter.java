package nextstep.jwp.adapter;

import nextstep.jwp.controller.LoginController;
import nextstep.jwp.model.httpmessage.request.HttpRequest;
import nextstep.jwp.model.httpmessage.response.HttpResponse;
import nextstep.jwp.view.ModelAndView;

import java.io.IOException;

public class LoginControllerHandlerAdapter implements HandlerAdapter {
    @Override
    public boolean supports(Object handler) {
        return (handler instanceof LoginController);
    }

    @Override
    public ModelAndView handle(HttpRequest request, HttpResponse response, Object handler) throws IOException {
        LoginController controller = (LoginController) handler;
        ModelAndView mv = new ModelAndView();
        controller.process(request, response, mv);
        return mv;
    }
}
