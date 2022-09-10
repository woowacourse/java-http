package customservlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.exception.AuthenticationException;
import nextstep.jwp.exception.BadRequestException;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.exception.NotFoundHandlerException;
import nextstep.jwp.presentation.controller.LoginPageRequestHandler;
import nextstep.jwp.presentation.controller.LoginRequestHandler;
import nextstep.jwp.presentation.controller.RegisterPageRequestHandler;
import nextstep.jwp.presentation.controller.RegisterRequestHandler;
import nextstep.jwp.presentation.controller.ResourceRequestHandler;
import nextstep.jwp.presentation.exceptionresolver.AuthenticationExceptionResolver;
import nextstep.jwp.presentation.exceptionresolver.BadRequestExceptionResolver;
import nextstep.jwp.presentation.exceptionresolver.NotFoundExceptionResolver;
import nextstep.jwp.presentation.filter.LoginInterceptor;
import org.apache.catalina.servlet.AbstractServlet;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;

public class ChicChocServlet extends AbstractServlet {

    private final LoginInterceptor loginInterceptor;
    private final ViewResolver viewResolver;
    private final List<RequestHandler> requestHandlers = new ArrayList<>();
    private final MappedExceptionResolvers mappedExceptionResolvers;

    public ChicChocServlet() {
        this.loginInterceptor = new LoginInterceptor();
        this.viewResolver = new ViewResolver();
        initRequestHandlers();
        this.mappedExceptionResolvers = initExceptionResolvers();
    }

    private void initRequestHandlers() {
        requestHandlers.addAll(
                List.of(new LoginPageRequestHandler(), new LoginRequestHandler(), new RegisterPageRequestHandler(),
                        new RegisterRequestHandler(), new ResourceRequestHandler()));
    }

    private MappedExceptionResolvers initExceptionResolvers() {
        final Map<Class<? extends RuntimeException>, ExceptionResolver> exceptionResolvers = Map.of(
                AuthenticationException.class, new AuthenticationExceptionResolver(),
                NotFoundException.class, new NotFoundExceptionResolver(),
                BadRequestException.class, new BadRequestExceptionResolver()
        );
        return MappedExceptionResolvers.from(exceptionResolvers);
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
        if (supportInterceptor(request)) {
            loginInterceptor.preHandle(request);
        }

        try {
            final var handler = getHandler(request);
            final var viewName = handler.handle(request, response);
            applyViewName(viewName, response);
        } catch (RuntimeException e) {
            mappedExceptionResolvers.resolveException(e, request, response);
        }
    }

    private boolean supportInterceptor(final HttpRequest request) {
        return loginInterceptor.support(request);
    }

    private RequestHandler getHandler(final HttpRequest request) {
        return requestHandlers.stream()
                .filter(it -> it.support(request))
                .findFirst()
                .orElseThrow(NotFoundHandlerException::new);
    }

    private void applyViewName(final String viewName, final HttpResponse response) {
        if (!Objects.isNull(viewName)) {
            viewResolver.resolve(viewName, response);
        }
    }
}
