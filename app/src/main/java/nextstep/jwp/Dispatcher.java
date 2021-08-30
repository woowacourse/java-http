package nextstep.jwp;

import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.handler.Handler;
import nextstep.jwp.handler.modelandview.ModelAndView;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.mapper.HandlerMapper;
import nextstep.jwp.view.View;
import nextstep.jwp.view.ViewResolver;

public class Dispatcher {

    private final HandlerMapper handlerMapper;
    private final ViewResolver viewResolver;

    public Dispatcher(HandlerMapper handlerMapper, ViewResolver viewResolver) {
        this.handlerMapper = handlerMapper;
        this.viewResolver = viewResolver;
    }

    public void dispatch(HttpRequest request, HttpResponse response) {
        ModelAndView modelAndView = service(request, response);
        response.setHttpStatus(modelAndView.getHttpStatus());

        View view = viewResolver.resolve(modelAndView.getViewName());
        view.render(modelAndView, response);
    }

    public ModelAndView service(HttpRequest request, HttpResponse response) {
        try {
            Handler handler = handlerMapper.mapping(request);
            return handler.service(request, response);
        } catch (NotFoundException notFoundException) {
            return ModelAndView.of("/404.html", HttpStatus.NOT_FOUND);
        } catch (Exception exception) {
            return ModelAndView.of("/500.html", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
