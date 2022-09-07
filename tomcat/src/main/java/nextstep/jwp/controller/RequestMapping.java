package nextstep.jwp.controller;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public enum RequestMapping {
    RESOURCE(HttpRequest::isResource, (request, response) -> new ResourceController().service(request, response)),
    LOGIN(request -> request.compareUrl("/login"), (request, response) -> new LoginController().service(request, response)),
    REGISTER(request -> request.compareUrl("/register"), (request, response) -> new RegisterController().service(request, response)),
    BASE(request -> request.compareUrl("/"), (request, response) -> new BaseController().service(request, response));

    Predicate<HttpRequest> predicate;
    MethodGenerator generator;

    RequestMapping(Predicate<HttpRequest> predicate, MethodGenerator generator) {
        this.predicate = predicate;
        this.generator = generator;
    }

    public static void mapping(HttpRequest request, HttpResponse response) throws Exception {
        Arrays.stream(values())
                .filter(mapping -> mapping.predicate.test(request))
                .map(mapping -> mapping.generator)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 url입니다."))
                .generate(request, response);
    }
}
