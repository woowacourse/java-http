package com.techcourse.servlet;

import com.techcourse.handler.ErrorHandler;
import com.techcourse.servlet.mapper.RequestMapping;
import com.techcourse.servlet.mapper.ViewMapping;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.http11.Dispatcher;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import com.techcourse.servlet.view.ViewResolver;

public class DispatcherServlet implements Dispatcher {

    @Override
    public void doDispatch(HttpRequest request, HttpResponse response) {
        try {
            Controller controller = getHandler(request);
            if (controller != null) {
                controller.service(request, response);
            } else {
                ViewResolver viewResolver = getViewResolver(request);
                viewResolver.resolve(request.getTargetPath()).render(response);
            }
        } catch (Exception exception) {
            ErrorHandler errorHandler = new ErrorHandler();
            errorHandler.handle(response, exception);
        }
    }

    public Controller getHandler(HttpRequest request) {
        RequestMapping requestMapping = new RequestMapping();
        return requestMapping.getController(request);
    }

    public ViewResolver getViewResolver(HttpRequest request) {
        ViewMapping viewMapping = new ViewMapping();
        return viewMapping.resolveView(request.getTargetPath());
    }
}
