package nextstep.jwp;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.mapper.ControllerMapper;
import nextstep.jwp.mapper.HandlerMapper;
import nextstep.jwp.mapper.ResourceMapper;
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
        View view = controllerMapper.mapping(requestLine);
        if(view.isEmpty()){
            view = resourceMapper.mapping(requestLine);
        }

        if(view.isEmpty()){
            throw new IllegalArgumentException("Bad request");
        }
        return HttpResponse.ok(viewResolver.resolve(view));
    }
}
