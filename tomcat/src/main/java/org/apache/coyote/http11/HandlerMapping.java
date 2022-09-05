package org.apache.coyote.http11;

import java.util.function.Function;
import java.util.stream.Stream;

import nextstep.jwp.handler.IndexHandler;
import nextstep.jwp.handler.LoginHandler;

public enum HandlerMapping {

    DEFAULT("/", IndexHandler::performBasicUrl),
    LOGIN("/login", LoginHandler::performLoginRequest),
    STATIC_FILE(Constants.NULL, ViewResolver::staticFileRequest),
    NOF_FOUND(Constants.NULL, HandlerMapping::returnNotFountResponse);

    private static class Constants {
        private static final String NULL = "null";
    }

    private final String url;
    private final Function<HttpRequest, HttpResponse> executor;

    HandlerMapping(String url, Function<HttpRequest, HttpResponse> executor) {
        this.url = url;
        this.executor = executor;
    }

    public static HandlerMapping findHandler(HttpRequest request) {
        String url = UriParser.extractUrl(request.getUri());
        if (FileExtension.hasFileExtension(url)) {
            return STATIC_FILE;
        }

        return Stream.of(HandlerMapping.values())
                .filter(v -> v.url.equals(url))
                .findAny()
                .orElse(NOF_FOUND);
    }

    public static HttpResponse returnNotFountResponse(HttpRequest request) {
        return new HttpResponse.Builder()
                .statusCode(HttpStatus.NOT_FOUND)
                .build();
    }

    public HttpResponse execute(HttpRequest request) {
        return this.executor.apply(request);
    }
}
