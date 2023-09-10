package nextstep.jwp;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpDispatcher;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import java.io.IOException;

public class JwpHttpDispatcher implements HttpDispatcher {

    private final HandlerMapping handlerMapping;

    public JwpHttpDispatcher(final HandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @Override
    public void doDispatch(final HttpRequest request, final HttpResponse response) throws IOException {
        if (request.isFile()) {
            response.setStatusCode(StatusCode.OK)
                    .setContentType(ContentType.findByPath(request.getPath()))
                    .setRedirect(request.getPath());
            return;
        }
        final HandlerExecutionChain handlerExecutionChain = handlerMapping.findController(request.getPath());
        if (!handlerExecutionChain.isHandlerNull()) {
            if (!handlerExecutionChain.applyPreHandle(request, response)) {
                return;
            }
            handlerExecutionChain.getHandler().service(request, response);
            return;
        }
        if (handlerExecutionChain.isHandlerNull()) {
            response.setStatusCode(StatusCode.NOT_FOUND)
                    .setContentType(ContentType.HTML)
                    .setRedirect("/404.html");
        }
    }


}
