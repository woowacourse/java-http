package org.apache.coyote.handler;

import org.apache.coyote.domain.HttpRequest;

public class HandlerMapping {

    public static Handler getHandler(HttpRequest httpRequest) {
        String uri = httpRequest.getUri();
        if (uri.contains("?")) {
            uri = uri.split("\\?")[0];
        }
        if(uri.contains(".")){
            System.out.println("static ");
            return new StaticFileHandler();
        }
        if(uri.contains("/login")){
            System.out.println("login ");
            return new LoginHandler();
        }
        throw new IllegalArgumentException("존재하지 않는 URI");
    }
}
