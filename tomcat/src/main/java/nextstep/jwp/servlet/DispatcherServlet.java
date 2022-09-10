package nextstep.jwp.servlet;


import nextstep.jwp.controller.Controller;
import nextstep.jwp.exception.CustomNotFoundException;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.interceptor.Interceptor;
import nextstep.jwp.interceptor.LoginInterceptor;
import nextstep.jwp.support.View;
import org.apache.catalina.core.Servlet;
import org.apache.catalina.exception.ResourceNotFoundException;
import org.apache.catalina.support.Resource;
import org.apache.coyote.HttpHeader;
import org.apache.coyote.HttpStatus;
import org.apache.coyote.support.Request;
import org.apache.coyote.support.Response;

import java.util.ArrayList;
import java.util.List;

public class DispatcherServlet implements Servlet {

    private final ControllerMapping controllerMapping = new ControllerMapping();
    private final List<Interceptor> interceptors = new ArrayList<>();

    public DispatcherServlet() {
        init();
    }

    @Override
    public void init() {
        interceptors.add(new LoginInterceptor(List.of("/login", "/register")));
    }

    @Override
    public void service(final Request request, final Response response) {
        for (Interceptor interceptor : interceptors) {
            if (!interceptor.preHandle(request, response)) {
                return;
            }
        }
        process(request, response);
    }

    private void process(final Request request, final Response response) {
        try {
            final Controller controller = controllerMapping.getController(request.getRequestInfo());
            controller.service(request, response);
        } catch (UnauthorizedException e) {
            makeRedirectResponse(View.UNAUTHORIZED.getValue(), response);
        } catch (CustomNotFoundException | ResourceNotFoundException e) {
            makeErrorResponse(HttpStatus.NOT_FOUND, View.NOT_FOUND, response);
        } catch (Exception e) {
            makeErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, View.INTERNAL_SERVER_ERROR, response);
        }
    }

    private void makeRedirectResponse(final String redirectUri, final Response response) {
        response.header(HttpHeader.LOCATION, redirectUri)
                .httpStatus(HttpStatus.FOUND);
    }

    private void makeErrorResponse(final HttpStatus httpStatus, final View errorView, final Response response) {
        final Resource resource = new Resource(errorView.getValue());
        response.header(HttpHeader.CONTENT_TYPE, resource.getContentType().getValue())
                .httpStatus(httpStatus)
                .content(resource.read());
    }
}
