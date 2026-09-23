package org.apache.coyote.http11.response;

import java.io.IOException;
import org.apache.coyote.http11.StaticResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InternalServerErrorHandler {
    private static final String SERVER_ERROR_PAGE = "/500.html";
    private static final Logger log = LoggerFactory.getLogger(InternalServerErrorHandler.class);

    private final StaticResourceLoader staticResourceLoader;

    public InternalServerErrorHandler() {
        this(new StaticResourceLoader());
    }

    InternalServerErrorHandler(StaticResourceLoader staticResourceLoader) {
        this.staticResourceLoader = staticResourceLoader;
    }

    public void handle(HttpResponse response) {
        response.reset();
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        try {
            response.setStaticResource(HttpStatus.INTERNAL_SERVER_ERROR, staticResourceLoader.load(SERVER_ERROR_PAGE));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            response.setBody("text/plain", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }
}
