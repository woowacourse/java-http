package org.apache.catalina.servlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.exception.AuthenticationException;
import nextstep.jwp.exception.DuplicateAccountException;
import nextstep.jwp.presentation.controller.LoginPageRequestHandler;
import nextstep.jwp.presentation.controller.LoginRequestHandler;
import nextstep.jwp.presentation.controller.RegisterPageRequestHandler;
import nextstep.jwp.presentation.controller.RegisterRequestHandler;
import nextstep.jwp.presentation.controller.RequestHandler;
import nextstep.jwp.presentation.filter.LoginFilter;
import org.apache.catalina.servlet.exception.NotFoundHandlerException;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.util.HttpStatus;

public class ChicChocServlet {

    private static final String UNAUTHORIZED_PAGE = "401";
    private static final String NOT_FOUND_PAGE = "404";
    private static final String INTERNAL_SERVER_ERROR_PAGE = "500";

    private final ViewResolver viewResolver;
    private final LoginFilter loginFilter;
    private final List<RequestHandler> requestHandlers = new ArrayList<>();

    public ChicChocServlet() {
        this.loginFilter = new LoginFilter();
        this.viewResolver = new ViewResolver();
    }

    public void doService(final Http11Request request, final Http11Response response) {
        initRequestHandlers();

//        if (supportFilter(request)) {
//            loginFilter.doFilter(request);
//        }

        String viewName = null;
        try {
            final var handler = getHandler(request);
            viewName = handler.handle(request, response);
        } catch (AuthenticationException e) {
            response.setStatusCode(HttpStatus.FOUND.getValue());
            response.setLocation(UNAUTHORIZED_PAGE);
        } catch (NotFoundHandlerException e) {
            response.setStatusCode(HttpStatus.FOUND.getValue());
            response.setLocation(NOT_FOUND_PAGE);
        } catch (DuplicateAccountException e) {
            response.setStatusCode(HttpStatus.FOUND.getValue());
            response.setLocation(INTERNAL_SERVER_ERROR_PAGE);
        }
        applyViewName(viewName, response);
    }

    private RequestHandler getHandler(final Http11Request request) {
        return requestHandlers.stream()
                .filter(it -> it.support(request))
                .findFirst()
                .orElseThrow(NotFoundHandlerException::new);
    }

    private void initRequestHandlers() {
        requestHandlers.addAll(
                List.of(new LoginPageRequestHandler(), new LoginRequestHandler(), new RegisterPageRequestHandler(),
                        new RegisterRequestHandler()));
    }

    private boolean supportFilter(final Http11Request request) {
        return loginFilter.support(request);
    }

    private void applyViewName(final String viewName, final Http11Response response) {
        if (!Objects.isNull(viewName)) {
            viewResolver.resolve(viewName, response);
        }
    }
}
