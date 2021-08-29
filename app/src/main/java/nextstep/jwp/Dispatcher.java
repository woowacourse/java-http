package nextstep.jwp;

import nextstep.jwp.adaptor.HandlerAdaptor;
import nextstep.jwp.exception.BadRequestException;
import nextstep.jwp.handler.Handler;
import nextstep.jwp.handler.modelandview.Model;
import nextstep.jwp.handler.modelandview.ModelAndView;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.mapper.HandlerMapper;
import nextstep.jwp.view.View;
import nextstep.jwp.view.ViewResolver;

public class Dispatcher {

    private final HandlerMapper handlerMapper;
    private final HandlerAdaptor handlerAdaptor;
    private final ViewResolver viewResolver;

    public Dispatcher(HandlerMapper handlerMapper, HandlerAdaptor handlerAdaptor, ViewResolver viewResolver) {
        this.handlerMapper = handlerMapper;
        this.handlerAdaptor = handlerAdaptor;
        this.viewResolver = viewResolver;
    }

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
