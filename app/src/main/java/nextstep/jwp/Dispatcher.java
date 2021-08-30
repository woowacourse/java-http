package nextstep.jwp;

import nextstep.jwp.adaptor.HandlerAdaptor;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.handler.Handler;
import nextstep.jwp.handler.ResponseEntity;
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

    public HttpResponse dispatch(HttpRequest httpRequest) {
        ResponseEntity response = service(httpRequest);

        ModelAndView modelAndView = response.modelAndView();
        Model model = modelAndView.getModel();
        View view = viewResolver.resolve(modelAndView.getViewName());

        return HttpResponse.of(model, view);
    }

    public ResponseEntity service(HttpRequest httpRequest) {
        try {
            Handler handler = handlerMapper.mapping(httpRequest);
            return handlerAdaptor.handle(handler, httpRequest);
        } catch (NotFoundException notFoundException) {
            return ResponseEntity.notFoundException();
        } catch (Exception exception) {
            return ResponseEntity.unhandledException();
        }
    }
}
