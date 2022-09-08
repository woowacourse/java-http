package org.apache.coyote.http11;

import org.apache.coyote.controller.Controller;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dispatcher {

    private static final Logger log = LoggerFactory.getLogger(Dispatcher.class);

    private final ViewResolver viewResolver;
    private final ErrorHandler errorHandler;

    public Dispatcher(final ViewResolver viewResolver, final ErrorHandler errorHandler) {
        this.viewResolver = viewResolver;
        this.errorHandler = errorHandler;
    }

    public void doDispatch(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        try {
            Controller controller = RequestMapping.find(httpRequest.getRequestUri());
            String response = controller.service(httpRequest, httpResponse);
            if (controller.isRest()) {
                httpResponse.setResponseBody(response);
                return;
            }
            viewResolver.resolve(response, httpResponse);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            errorHandler.handle(e, httpResponse);
        }
    }
}
