package nextstep.jwp;

import nextstep.jwp.exception.BadRequestException;
import nextstep.jwp.handler.Handler;
import nextstep.jwp.handler.modelandview.Model;
import nextstep.jwp.handler.modelandview.ModelAndView;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.mapper.HandlerMapper;
import nextstep.jwp.mapper.HandlerMapperImpl;
import nextstep.jwp.view.View;
import nextstep.jwp.view.ViewResolver;

public class Dispatcher {

    private final HandlerMapper handlerMappers = new HandlerMapperImpl();
    private final ViewResolver viewResolver = new ViewResolver();

    public HttpResponse execute(HttpRequest httpRequest) {
        ModelAndView modelAndView = service(httpRequest);
        Model model = modelAndView.getModel();
        View view = viewResolver.resolve(modelAndView.getViewName());

        return HttpResponse.of(model, view);
    }

    public ModelAndView service(HttpRequest httpRequest) {
        try {
            Handler handler = handlerMappers.findHandler(httpRequest.requestLine());
            return handler.service(httpRequest);
        } catch (BadRequestException badRequestException) {
            return ModelAndView.badRequest();
        } catch (Exception exception) {
            return ModelAndView.error();
        }
    }
}
