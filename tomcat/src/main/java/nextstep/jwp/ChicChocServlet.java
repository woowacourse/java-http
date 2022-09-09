package nextstep.jwp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.exception.AuthenticationException;
import nextstep.jwp.exception.DuplicateAccountException;
import nextstep.jwp.exception.NotFoundHandlerException;
import nextstep.jwp.exception.NotFoundResourceException;
import nextstep.jwp.presentation.controller.LoginPageRequestHandler;
import nextstep.jwp.presentation.controller.LoginRequestHandler;
import nextstep.jwp.presentation.controller.RegisterPageRequestHandler;
import nextstep.jwp.presentation.controller.RegisterRequestHandler;
import nextstep.jwp.presentation.controller.RequestHandler;
import nextstep.jwp.presentation.controller.ResourceRequestHandler;
import nextstep.jwp.presentation.filter.LoginFilter;
import org.apache.catalina.servlet.AbstractServlet;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.util.HttpStatus;

public class ChicChocServlet extends AbstractServlet {

    private static final String UNAUTHORIZED_PAGE = "401";
    private static final String NOT_FOUND_PAGE = "404";
    private static final String INTERNAL_SERVER_ERROR_PAGE = "500";

    private final ViewResolver viewResolver;
    private final LoginFilter loginFilter;
    private final List<RequestHandler> requestHandlers = new ArrayList<>();

    public ChicChocServlet() {
        this.loginFilter = new LoginFilter();
        this.viewResolver = new ViewResolver();
        initRequestHandlers();
    }

    private void initRequestHandlers() {
        requestHandlers.addAll(
                List.of(new LoginPageRequestHandler(), new LoginRequestHandler(), new RegisterPageRequestHandler(),
                        new RegisterRequestHandler(), new ResourceRequestHandler()));
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        processRequest(request, response);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        processRequest(request, response);
    }

    public void processRequest(final HttpRequest request, final HttpResponse response) {
        if (supportFilter(request)) {
            loginFilter.doFilter(request);
        }

        String viewName = null;
        try {
            final var handler = getHandler(request);
            viewName = handler.handle(request, response);
        } catch (AuthenticationException e) {
            response.setStatusCode(HttpStatus.FOUND);
            response.setLocation(UNAUTHORIZED_PAGE);
        } catch (NotFoundHandlerException | NotFoundResourceException e) {
            response.setStatusCode(HttpStatus.FOUND);
            response.setLocation(NOT_FOUND_PAGE);
        } catch (DuplicateAccountException e) {
            response.setStatusCode(HttpStatus.FOUND);
            response.setLocation(INTERNAL_SERVER_ERROR_PAGE);
        }
        applyViewName(viewName, response);
    }

    private RequestHandler getHandler(final HttpRequest request) {
        return requestHandlers.stream()
                .filter(it -> it.support(request))
                .findFirst()
                .orElseThrow(NotFoundHandlerException::new);
    }

    private boolean supportFilter(final HttpRequest request) {
        return loginFilter.support(request);
    }

    private void applyViewName(final String viewName, final HttpResponse response) {
        if (!Objects.isNull(viewName)) {
            viewResolver.resolve(viewName, response);
        }
    }
}
