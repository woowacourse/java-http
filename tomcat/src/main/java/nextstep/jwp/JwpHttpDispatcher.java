package nextstep.jwp;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.HttpDispatcher;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import java.io.IOException;
import java.net.URL;

public class JwpHttpDispatcher implements HttpDispatcher {

    private static final String STATIC = "static";

    private final HandlerResolver handlerResolver;

    public JwpHttpDispatcher(final HandlerResolver handlerResolver) {
        this.handlerResolver = handlerResolver;
    }

    @Override
    public void handle(final HttpRequest request, final HttpResponse response) throws IOException {
        final Controller controller = handlerResolver.resolve(request.getHttpMethod(), request.getPath());
        if (controller != null) {
            controller.service(request, response);
            return;
        }
        final var resource = getClass().getClassLoader().getResource(STATIC + request.getPath());
        if (controller == null && resource == null) {
            final var notFoundResource = getClass().getClassLoader().getResource(STATIC + "/404.html");
            setResponse(response, StatusCode.NOT_FOUND, notFoundResource);
            return;
        }
        setResponse(response, StatusCode.OK, resource);
    }

    private void setResponse(final HttpResponse response, final StatusCode statusCode, final URL resource) throws IOException {
        response.setStatusCode(statusCode);
        response.setContentType(ContentType.HTML);
        response.setResponseBodyByUrl(resource);
    }

}
