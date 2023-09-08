package nextstep.jwp;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpDispatcher;
import org.apache.coyote.http11.HttpException;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import java.io.IOException;
import java.net.URL;

public class JwpHttpDispatcher implements HttpDispatcher {

    private static final String STATIC = "static";

    private final HandlerMapping handlerMapping;

    public JwpHttpDispatcher(final HandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @Override
    public void doDispatch(final HttpRequest request, final HttpResponse response) throws IOException {
        final var resource = getClass().getClassLoader().getResource(STATIC + request.getPath());
        if (resource != null && isFile(request.getPath())) {
            setResponse(response, StatusCode.OK, ContentType.findByPath(request.getPath()), resource);
            return;
        }
        final HandlerExecutionChain handlerExecutionChain = handlerMapping.findHandler(request.getHttpMethod(), request.getPath());
        if (!handlerExecutionChain.isHandlerNull()) {
            if (!handlerExecutionChain.applyPreHandle(request, response)) {
                return;
            }
            handlerExecutionChain.getHandler().service(request, response);
            return;
        }
        if (handlerExecutionChain.isHandlerNull()) {
            final var notFoundResource = getClass().getClassLoader().getResource(STATIC + "/404.html");
            setResponse(response, StatusCode.NOT_FOUND, ContentType.HTML, notFoundResource);
        }
    }

    private boolean isFile(final String path) {
        try {
            ContentType.findByPath(path);
        } catch (final HttpException exception) {
            return false;
        }
        return true;
    }

    private void setResponse(final HttpResponse response, final StatusCode statusCode, final ContentType contentType, final URL resource) throws IOException {
        response.setStatusCode(statusCode);
        response.setContentType(contentType);
        response.setResponseBodyByUrl(resource);
    }

}
