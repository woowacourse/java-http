package servlet;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.ExceptionHandler;
import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import servlet.mapping.ExceptionMappingImpl;
import servlet.mapping.ExceptionMapping;
import servlet.mapping.HandlerMapping;
import servlet.mapping.HandlerMappingImpl;
import servlet.mapping.ResponseEntity;
import servlet.view.ViewResolver;

public class ServletImpl implements Servlet {

    private final ExceptionMapping exceptionMapping;
    private final HandlerMapping handlerMapping;
    private final ViewResolver viewResolver;

    public ServletImpl() {
        this.handlerMapping = new HandlerMappingImpl(new Controller(new UserService()));
        this.viewResolver = new ViewResolver();
        this.exceptionMapping = new ExceptionMappingImpl(new ExceptionHandler());
    }

    @Override
    public String doService(String input) {
        HttpRequest request = new HttpRequest(input);
        ResponseEntity entity;
        try {
            entity = handlerMapping.map(request);
        } catch (Exception e) {
            entity = exceptionMapping.map(e);
        }
        HttpResponse httpResponse = viewResolver.getResponse(entity.getMethod(), entity.getUri(), entity.getStatus());
        return httpResponse.getResponse();
    }
}
