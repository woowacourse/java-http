package nextstep.jwp;

import nextstep.jwp.adaptor.HandlerAdaptor;
import nextstep.jwp.exception.BadRequestException;
import nextstep.jwp.handler.Handler;
import nextstep.jwp.adaptor.HandlerAdaptorImpl;
import nextstep.jwp.handler.modelandview.Model;
import nextstep.jwp.handler.modelandview.ModelAndView;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.mapper.HandlerMapper;
import nextstep.jwp.mapper.HandlerMapperImpl;
import nextstep.jwp.view.View;
import nextstep.jwp.view.ViewResolver;

public class Dispatcher {

    private final HandlerMapper handlerMapper = new HandlerMapperImpl();
    private final HandlerAdaptor handlerAdaptor = new HandlerAdaptorImpl();
    private final ViewResolver viewResolver = new ViewResolver();

    public HttpResponse execute(HttpRequest httpRequest) {
        ModelAndView modelAndView = service(httpRequest);

        Model model = modelAndView.getModel();
        View view = viewResolver.resolve(modelAndView.getViewName());

        return HttpResponse.of(model, view);
    }

    public ModelAndView service(HttpRequest httpRequest) {
        try {
            Handler handler = handlerMapper.findHandler(httpRequest.requestLine());
            return handlerAdaptor.handle(handler, httpRequest);
        } catch (BadRequestException badRequestException) {
            return ModelAndView.badRequest();
        } catch (Exception exception) {
            exception.printStackTrace();
            return ModelAndView.error();
        }
    }
}
