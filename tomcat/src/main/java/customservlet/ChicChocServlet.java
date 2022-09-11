package customservlet;

import java.util.Objects;
import org.apache.catalina.servlet.AbstractServlet;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;

public class ChicChocServlet extends AbstractServlet {

    private final ViewResolver viewResolver;
    private final MappedRequestHandlers mappedRequestHandlers;
    private final MappedExceptionResolvers mappedExceptionResolvers;

    public ChicChocServlet(final ViewResolver viewResolver, final MappedRequestHandlers mappedRequestHandlers,
                           final MappedExceptionResolvers mappedExceptionResolvers) {
        this.viewResolver = viewResolver;
        this.mappedRequestHandlers = mappedRequestHandlers;
        this.mappedExceptionResolvers = mappedExceptionResolvers;
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        processRequest(request, response);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        processRequest(request, response);
    }

    private void processRequest(final HttpRequest request, final HttpResponse response) {
        try {
            final var handler = mappedRequestHandlers.getHandler(request);
            final var viewName = handler.handle(request, response);
            applyViewName(viewName, response);
        } catch (RuntimeException e) {
            final var exceptionResolver = mappedExceptionResolvers.getResolver(e);
            exceptionResolver.resolveException(request, response);
        }
    }

    private void applyViewName(final String viewName, final HttpResponse response) {
        if (!Objects.isNull(viewName)) {
            viewResolver.resolve(viewName, response);
        }
    }
}
