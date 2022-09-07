package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.Controller;
import org.apache.coyote.ExceptionHandler;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.header.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static ExceptionHandler exceptionHandler = null;

    public static void setExceptionHandler(final ExceptionHandler handler) {
        exceptionHandler = handler;
    }

    public static HttpResponse handle(final HttpRequest httpRequest) throws IOException, URISyntaxException {
        final Controller controller = RequestMapping.getController(httpRequest);
        try {
            return controller.service(httpRequest);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return handleException(e);
        }
    }

    private static HttpResponse handleException(final Exception e) throws IOException, URISyntaxException {
        if (exceptionHandler != null) {
            return exceptionHandler.handle(e);
        }
        return new HttpResponse(StatusCode.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    private RequestHandler() {
    }
}
