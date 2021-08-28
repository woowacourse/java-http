package nextstep.jwp;

import java.util.Objects;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.mapper.ControllerMapper;
import nextstep.jwp.mapper.HandlerMapper;
import nextstep.jwp.mapper.ResourceMapper;
import nextstep.jwp.handler.ModelAndView;
import nextstep.jwp.view.View;
import nextstep.jwp.view.ViewResolver;

public class Executor {

    private final HandlerMapper controllerMapper = new ControllerMapper();
    private final HandlerMapper resourceMapper = new ResourceMapper();
    private final ViewResolver viewResolver = new ViewResolver();

    public HttpResponse service(HttpRequest httpRequest) {
        return service(httpRequest.getRequestLine());
    }

    public HttpResponse service(RequestLine requestLine) {
        // TODO :: HandlerMapper로 한번에 처리
        ModelAndView modelAndView = controllerMapper.mapping(requestLine);

        if(Objects.isNull(modelAndView)){
            modelAndView = resourceMapper.mapping(requestLine);
        }

        if(Objects.isNull(modelAndView)){
            throw new IllegalArgumentException("Bad request");
        }

        View view = viewResolver.resolve(modelAndView.getViewName());
        return view.render(modelAndView.getModel());
    }
}
