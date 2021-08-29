package nextstep.jwp;

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

        // TODO :: Exception handler
        Handler handler = handlerMappers.findHandler(httpRequest.requestLine());
        ModelAndView modelAndView = handler.service(httpRequest);

        Model model = modelAndView.getModel();
        View view = viewResolver.resolve(modelAndView.getViewName());

        return HttpResponse.of(model, view);
    }

//    public ModelAndView service(Object handleObject){
//        try {
//            Handler handler = handlerMappers.findHandler(handleObject);
//            return handler.service(handleObject);
//        } catch (Exception e){
//            return service(e);
//        }
//    }
}
