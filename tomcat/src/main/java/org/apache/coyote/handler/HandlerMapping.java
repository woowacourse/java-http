package org.apache.coyote.handler;

import org.apache.coyote.domain.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandlerMapping {

    private static final Logger log = LoggerFactory.getLogger(RegisterHandler.class);

    public static Handler getHandler(HttpRequest httpRequest) {
        String uri = httpRequest.getUri();
        if (uri.contains("?")) {
            uri = uri.split("\\?")[0];
        }
        if(uri.contains(".")){
            return new StaticFileHandler();
        }
        if(uri.contains("/login")){
            return new LoginHandler();
        }
        if(uri.contains("/register")){
            return new RegisterHandler();
        }
        throw new IllegalArgumentException("존재하지 않는 URI");
    }
}
