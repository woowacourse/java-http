package org.apache.coyote.http11;

import java.util.function.Function;

import nextstep.jwp.handler.IndexHandler;
import nextstep.jwp.handler.LoginHandler;

public class HandlerMapping {

    public Function<HttpRequest, HttpResponse> findController(HttpRequest request) {
        String url = UriParser.extractUrl(request.getUri());

        if (FileExtension.hasFileExtension(url)) {
            return ViewResolver::staticFileRequest;
        }

        if (url.equals("/")) {
            return IndexHandler::performBasicUrl;
        }

        if (url.equals("/login")) {
            return LoginHandler::performLoginRequest;
        }
        return IndexHandler::returnNotFountResponse;
    }
}
