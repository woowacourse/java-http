package org.apache.coyote.http11;

import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.coyote.http11.exception.ResourceNotFoundException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public enum HandlerMapping {

    HOME("/", httpRequest -> {
        HomeController homeController = new HomeController();
        return homeController.service(httpRequest);
    }),
    LOGIN("/login", httpRequest -> {
        final LoginController loginController = new LoginController();
        return loginController.service(httpRequest);
    }),
    REGISTER("/register", httpRequest -> {
        final RegisterController registerController = new RegisterController();
        return registerController.service(httpRequest);
    }),
    STATIC_RESOURCE(null, httpRequest -> {
        final StaticResourceController staticResourceController = new StaticResourceController();
        return staticResourceController.service(httpRequest);
    }),
    NOT_FOUND(null, httpRequest -> {
        final NotFoundController notFoundController = new NotFoundController();
        return notFoundController.service(httpRequest);
    }),
    LOGIN_FAILED(null, httpRequest -> {
        final LoginFailedController loginFailedController = new LoginFailedController();
        return loginFailedController.service(httpRequest);
    })
    ;

    private final String path;
    private final Function<HttpRequest, HttpResponse> function;

    HandlerMapping(String path,
                   Function<HttpRequest, HttpResponse> function) {
        this.path = path;
        this.function = function;
    }

    public static HandlerMapping findHandler(HttpRequest httpRequest) {
        final String resource = httpRequest.getPath().getResource();

        if (httpRequest.isStaticResource()) {
            return STATIC_RESOURCE;
        }

        return Stream.of(values())
                .filter(it -> it.path.equals(resource))
                .findAny()
                .orElseThrow(ResourceNotFoundException::new);
    }

    public HttpResponse doService(HttpRequest httpRequest) {
        return function.apply(httpRequest);
    }
}
