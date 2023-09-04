package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestTarget;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathRequestHandler implements RequestHandler {

    private static final List<String> HANDLEABLE_PATH = List.of("/", "/login");
    private static final Logger log = LoggerFactory.getLogger(PathRequestHandler.class);
    private final StaticResourceHandler staticResourceHandler;

    public PathRequestHandler(final StaticResourceHandler staticResourceHandler) {
        this.staticResourceHandler = staticResourceHandler;
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        final HttpRequestTarget target = new HttpRequestTarget(httpRequest.getRequestTarget());

        if (target.getPath().equals("/")) {
            return HttpResponse.withBody(
                "HTTP/1.1 200 OK ",
                "text/html;charset=utf-8",
                "Hello world!"
            );
        }

        if (target.getPath().equals("/login")) {
            if (target.containsParameterKey("account") && target.containsParameterKey("password")) {
                final String account = target.getParameterValue("account");
                final Optional<User> byAccount = InMemoryUserRepository.findByAccount(account);
                byAccount.ifPresent(user -> log.info("user = {}", user));
            }
            final HttpRequest convertedRequest = new HttpRequest(
                "GET /login.html HTTP.1,1",
                httpRequest.getHeaders(),
                httpRequest.getBody()
            );
            return staticResourceHandler.handle(convertedRequest);
        }

        return new HttpResponse("HTTP/1.1 404 Not Found", "", Collections.emptyList());
    }

    @Override
    public boolean handleable(final HttpRequest httpRequest) {
        final HttpRequestTarget target = new HttpRequestTarget(httpRequest.getRequestTarget());

        return HANDLEABLE_PATH.contains(target.getPath());
    }
}
