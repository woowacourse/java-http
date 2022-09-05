package org.apache.coyote.http11;

import java.util.function.Function;

public class HandlerMapping {

    private final Controller controller;

    public HandlerMapping() {
        this.controller = new Controller();
    }

    public Function<HttpRequest, HttpResponse> findController(HttpRequest request) {
        String url = UriParser.extractUrl(request.getUri());

        if (FileExtension.hasFileExtension(url)) {
            return ViewResolver::staticFileRequest;
        }

        if (url.equals("/")) {
            return controller::performBasicUrl;
        }
        if (url.equals("/login")) {
            return controller::performLoginRequest;
        }
        return controller::returnNotFountResponse;
    }
}
