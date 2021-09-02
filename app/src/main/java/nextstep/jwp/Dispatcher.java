package nextstep.jwp;

import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.handler.Handler;
import nextstep.jwp.handler.modelandview.ModelAndView;
import nextstep.jwp.http.cookie.HttpCookie;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.mapper.HandlerMappers;
import nextstep.jwp.view.View;
import nextstep.jwp.view.ViewResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dispatcher {

    private static final Logger log = LoggerFactory.getLogger(Dispatcher.class);

    private final HandlerMappers handlerMappers;
    private final ViewResolver viewResolver;

    public Dispatcher(HandlerMappers handlerMappers, ViewResolver viewResolver) {
        this.handlerMappers = handlerMappers;
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
            Handler handler = handlerMappers.mapping(request);
            return handler.handle(request, response);
        } catch (NotFoundException notFoundException) {
            return ModelAndView.of("/404.html", HttpStatus.NOT_FOUND);
        } catch (Exception exception) {
            return ModelAndView.of("/500.html", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
