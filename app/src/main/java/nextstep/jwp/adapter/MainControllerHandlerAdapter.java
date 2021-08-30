package nextstep.jwp.adapter;

import nextstep.jwp.controller.MainController;
import nextstep.jwp.model.httpmessage.request.HttpRequest;
import nextstep.jwp.model.httpmessage.response.HttpResponse;
import nextstep.jwp.view.ModelAndView;

import java.io.IOException;

public class MainControllerHandlerAdapter implements HandlerAdapter {
    @Override
    public boolean supports(Object handler) {
        return (handler instanceof MainController);
    }

    @Override
    public ModelAndView handle(HttpRequest request, HttpResponse response, Object handler) throws IOException {
        MainController controller = (MainController) handler;
        ModelAndView mv = new ModelAndView();
        controller.process(request, response, mv);
        return mv;
    }
}
