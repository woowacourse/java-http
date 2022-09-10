package nextstep.jwp;

import static org.apache.coyote.http11.util.HttpStatus.BAD_REQUEST;
import static org.apache.coyote.http11.util.HttpStatus.FOUND;
import static org.apache.coyote.http11.util.HttpStatus.NOT_FOUND;
import static org.apache.coyote.http11.util.HttpStatus.UNAUTHORIZED;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.exception.AuthenticationException;
import nextstep.jwp.exception.DuplicateAccountException;
import nextstep.jwp.exception.InvalidSessionException;
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

public class ChicChocServlet extends AbstractServlet {

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
            response.setStatusCode(FOUND);
            viewName = UNAUTHORIZED.getValue();
        } catch (NotFoundHandlerException | NotFoundResourceException e) {
            response.setStatusCode(FOUND);
            viewName = NOT_FOUND.getValue();
        } catch (DuplicateAccountException | InvalidSessionException e) {
            response.setStatusCode(FOUND);
            viewName = BAD_REQUEST.getValue();
        } finally {
            applyViewName(viewName, response);
        }
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
