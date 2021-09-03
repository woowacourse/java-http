package nextstep.jwp.adapter;

import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.model.httpmessage.request.HttpRequest;
import nextstep.jwp.model.httpmessage.response.HttpResponse;
import nextstep.jwp.view.ModelAndView;

import java.io.IOException;

public class RegisterControllerHandlerAdapter implements HandlerAdapter {
    @Override
    public boolean supports(Object handler) {
        return (handler instanceof RegisterController);
    }

    @Override
    public ModelAndView handle(HttpRequest request, HttpResponse response, Object handler) throws IOException {
        RegisterController controller = (RegisterController) handler;
        ModelAndView mv = new ModelAndView();
        controller.process(request, response, mv);
        return mv;
    }
}
