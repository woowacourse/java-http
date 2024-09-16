package org.apache.coyote.dispatcher;

import com.techcourse.controller.Controller;
import com.techcourse.controller.ControllerAdviser;
import com.techcourse.controller.RequestMapping;
import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class DispatcherServlet {

    private final RequestMapping requestMapping;

    private DispatcherServlet() {
        requestMapping = new RequestMapping();
    }

    public static DispatcherServlet getInstance() {
        return DispatcherServletHolder.INSTANCE;
    }

    public void service(HttpRequest request, HttpResponse response) {
        try {
            Controller controller = requestMapping.getController(request);
            controller.service(request, response);
        } catch (UncheckedServletException e) {
            ControllerAdviser.service(e, request, response);
        }
    }

    private static class DispatcherServletHolder {

        private static final DispatcherServlet INSTANCE = new DispatcherServlet();
    }
}
