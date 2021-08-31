package nextstep.jwp.adapter;

import nextstep.jwp.controller.DefaultController;
import nextstep.jwp.model.httpmessage.request.HttpRequest;
import nextstep.jwp.model.httpmessage.response.HttpResponse;
import nextstep.jwp.util.FileUtils;
import nextstep.jwp.view.ModelAndView;

import java.io.IOException;

import static nextstep.jwp.model.httpmessage.common.ContentType.HTML;

public class DefaultControllerHandlerAdapter implements HandlerAdapter {
    @Override
    public boolean supports(Object handler) {
        return (handler instanceof DefaultController);
    }

    @Override
    public ModelAndView handle(HttpRequest request, HttpResponse response, Object handler) throws IOException {
        DefaultController controller = (DefaultController) handler;
        ModelAndView mv = new ModelAndView();
        String body = FileUtils.readFileOfUrl(request.getRequestURI() + HTML.suffix());
        mv.getModel().put("body", body);
        controller.process(request, response, mv);
        return mv;
    }
}
