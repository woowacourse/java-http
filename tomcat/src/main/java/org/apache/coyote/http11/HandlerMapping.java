package org.apache.coyote.http11;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

import org.apache.coyote.http11.model.ContentType;
import org.apache.coyote.http11.model.RequestParser;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;

import nextstep.jwp.handler.AbstractController;
import nextstep.jwp.handler.IndexHandler;
import nextstep.jwp.handler.LoginHandler;
import nextstep.jwp.handler.RegisterHandler;
import nextstep.jwp.handler.ResourceHandler;

public enum HandlerMapping {

    DEFAULT("/", (request, response) -> new IndexHandler().service(request, response)),
    LOGIN("/login", (request, response) -> new LoginHandler().service(request, response)),
    REGISTER("/register", (request, response) -> new RegisterHandler().service(request, response)),
    STATIC_FILE(Constants.NULL, (request, response) -> new ResourceHandler().service(request, response)),
    NOF_FOUND(Constants.NULL, (request, response) -> new NotMappedHandler().service(request, response));

    private static class Constants {
        private static final String NULL = "null";
    }

    private final String url;
    private final BiConsumer<HttpRequest, HttpResponse> executor;

    HandlerMapping(String url, BiConsumer<HttpRequest, HttpResponse> executor) {
        this.url = url;
        this.executor = executor;
    }

    public static HandlerMapping findHandler(HttpRequest request) {
        String url = RequestParser.extractUrl(request.getUri());
        if (ContentType.hasFileExtension(url)) {
            return STATIC_FILE;
        }

        return Stream.of(HandlerMapping.values())
                .filter(v -> v.url.startsWith(url))
                .findAny()
                .orElse(NOF_FOUND);
    }

    public void execute(HttpRequest request, HttpResponse response) {
        this.executor.accept(request, response);
    }

    static class NotMappedHandler extends AbstractController {
    }
}
