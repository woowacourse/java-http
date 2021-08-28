package nextstep.jwp.infrastructure.http;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.infrastructure.http.request.HttpMethod;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.request.HttpRequestLine;
import nextstep.jwp.infrastructure.http.request.URI;
import nextstep.jwp.infrastructure.http.response.HttpResponse;
import nextstep.jwp.infrastructure.http.response.HttpStatusCode;
import nextstep.jwp.infrastructure.http.response.HttpStatusLine;

public enum ControllerMapping {
    hello(new HttpRequestLine(HttpMethod.GET, "/"),
        (request) -> View.buildByResource("/hello.html")),

    login(new HttpRequestLine(HttpMethod.GET, "/login"),
        (request) -> {
            final URI uri = request.getRequestLine().getUri();

            if (uri.hasKeys("account", "password")) {
                String account = uri.getValue("account");
                String password = uri.getValue("password");
                String location = "/401.html";
                if (InMemoryUserRepository.existsByAccountAndPassword(account, password)) {
                    location = "/index.html";
                }
                return View.buildByHttpResponse(
                    new HttpResponse(
                        new HttpStatusLine(HttpStatusCode.FOUND),
                        new HttpHeaders.Builder()
                            .header("Location", location)
                            .build()
                    )
                );
            }

            return View.buildByResource("/login.html");
        });

    private static final Map<HttpRequestLine, Function<HttpRequest, View>> CONTROLLERS = Arrays.stream(values())
        .collect(Collectors.toMap(ControllerMapping::getHttpRequestLine, ControllerMapping::getController));

    private final HttpRequestLine httpRequestLine;

    private final Function<HttpRequest, View> controller;

    ControllerMapping(final HttpRequestLine httpRequestLine,
                      final Function<HttpRequest, View> controller) {
        this.httpRequestLine = httpRequestLine;
        this.controller = controller;
    }

    public static View handle(final HttpRequest request) {
        final HttpRequestLine requestLine = request.getRequestLine();
        final HttpRequestLine requestLineWithoutQuery = new HttpRequestLine(requestLine.getHttpMethod(), requestLine.getUri().getBaseUri());
        if (!contains(request)) {
            return View.buildByResource(HttpStatusCode.NOT_FOUND, "/404.html");
        }

        return CONTROLLERS.get(requestLineWithoutQuery).apply(request);
    }

    public static boolean contains(final HttpRequest request) {
        final HttpRequestLine requestLine = request.getRequestLine();
        final HttpRequestLine requestLineWithoutQuery = new HttpRequestLine(requestLine.getHttpMethod(), requestLine.getUri().getBaseUri());
        return CONTROLLERS.containsKey(requestLineWithoutQuery);
    }

    private Function<HttpRequest, View> getController() {
        return controller;
    }

    public HttpRequestLine getHttpRequestLine() {
        return httpRequestLine;
    }
}

