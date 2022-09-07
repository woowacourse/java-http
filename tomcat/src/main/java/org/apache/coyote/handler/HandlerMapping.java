package org.apache.coyote.handler;

import org.apache.coyote.domain.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandlerMapping {

    private static final Logger log = LoggerFactory.getLogger(RegisterHandler.class);

    public static Handler getHandler(HttpRequest httpRequest) {
        String uri = httpRequest.getRequestLine().getPath().getPath();
        log.info("[Handler Mapping] URL : " + uri);
        if (uri.equals("/")) {
            log.info("[Handler Mapping] Home Calling : " + uri);
            uri = "/index.html";
        }
        if (uri.contains(".")) {
            log.info("[Handler Mapping] Static File Calling : " + uri);
            return new StaticFileHandler();
        }
        if (uri.contains("/login")) {
            log.info("[Handler Mapping] Login Handler Calling : " + uri);
            return new LoginHandler();
        }
        if (uri.contains("/register")) {
            log.info("[Handler Mapping] Register Handler Calling : " + uri);
            return new RegisterHandler();
        }
        throw new IllegalArgumentException("존재하지 않는 URI");
    }
}
