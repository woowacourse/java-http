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

public enum ControllerMapping {
    hello(new HttpRequestLine(HttpMethod.GET, "/"),
        (request) -> "/hello.html"),

    login(new HttpRequestLine(HttpMethod.GET, "/login"),
        (request) -> {
            final URI uri = request.getRequestLine().getUri();

            if (uri.hasKeys("account", "password")) {
                String account = uri.getValue("account");
                String password = uri.getValue("password");
                if (InMemoryUserRepository.existsByAccountAndPassword(account, password)) {
                    System.out.printf("Successful login (%s)%n", account);
                } else {
                    System.out.printf("Fail To login (%s)%n", account);
                }
            }

            return "/login.html";
        });

    private static final Map<HttpRequestLine, Function<HttpRequest, String>> CONTROLLERS = Arrays.stream(values())
        .collect(Collectors.toMap(ControllerMapping::getHttpRequestLine, ControllerMapping::getController));

    private final HttpRequestLine httpRequestLine;

    private final Function<HttpRequest, String> controller;

    ControllerMapping(final HttpRequestLine httpRequestLine,
                      final Function<HttpRequest, String> controller) {
        this.httpRequestLine = httpRequestLine;
        this.controller = controller;
    }

    public static String handle(final HttpRequest request) {
        HttpRequestLine requestLine = request.getRequestLine();
        final HttpRequestLine requestLineWithoutQuery = new HttpRequestLine(requestLine.getHttpMethod(), requestLine.getUri().getBaseUri());
        if (!contains(request)) {
            return "/404.html";
        }

        return CONTROLLERS.get(requestLineWithoutQuery).apply(request);
    }

    public static boolean contains(final HttpRequest request) {
        final HttpRequestLine requestLine = request.getRequestLine();
        final HttpRequestLine requestLineWithoutQuery = new HttpRequestLine(requestLine.getHttpMethod(), requestLine.getUri().getBaseUri());
        return CONTROLLERS.containsKey(requestLineWithoutQuery);
    }

    private Function<HttpRequest, String> getController() {
        return controller;
    }

    public HttpRequestLine getHttpRequestLine() {
        return httpRequestLine;
    }
}

