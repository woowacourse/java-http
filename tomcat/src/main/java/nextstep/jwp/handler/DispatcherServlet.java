package nextstep.jwp.handler;

import org.apache.catalina.servlet.HttpServlet;
import org.apache.coyote.http.vo.HttpRequest;
import org.apache.coyote.http.vo.HttpResponse;

public class DispatcherServlet implements HttpServlet {

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        final Handler handler = getSupportedHandler(httpRequest);
        return handler.handle(httpRequest);
    }

    @Override
    public boolean isSupported(final HttpRequest httpRequest) {
        return HandlerMapping.hasSupportedHandler(httpRequest);
    }

    private Handler getSupportedHandler(final HttpRequest httpRequest) {
        return HandlerMapping.getSupportedHandler(httpRequest);
    }
}
