package org.apache.coyote.http11.handle.handler.custom;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.apache.coyote.http11.handle.HttpHandlerCondition;
import org.apache.coyote.http11.handle.handler.HttpHandler;
import org.apache.coyote.http11.handle.handler.resource.HtmlHttpHandler;
import org.apache.coyote.http11.reqeust.HttpMethod;
import org.apache.coyote.http11.reqeust.HttpRequest;
import org.apache.coyote.http11.reqeust.QueryParameters;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHttpHandler implements HttpHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginHttpHandler.class);

    private static final LoginHttpHandler instance = new LoginHttpHandler();

    private final HtmlHttpHandler htmlHttpHandler = HtmlHttpHandler.getInstance();

    private final Map<HttpHandlerCondition, Function<HttpRequest, HttpResponse>> handlerMethodMapper = Map.of(
            new HttpHandlerCondition(HttpMethod.GET, "/login"), this::handleGetLogin
    );

    private LoginHttpHandler() {
    }

    @Override
    public HttpResponse handle(final HttpRequest request) {
        final HttpHandlerCondition requestCondition = HttpHandlerCondition.from(request);
        final Function<HttpRequest, HttpResponse> handlerMethod = handlerMethodMapper.get(requestCondition);
        if (handlerMethod == null) {
            throw new IllegalStateException("해당 요청을 처리할 수 없는 핸들러입니다. " + requestCondition);
        }

        return handlerMethod.apply(request);
    }

    @Override
    public boolean canHandle(final HttpRequest request) {
        final HttpHandlerCondition requestCondition = HttpHandlerCondition.from(request);

        return handlerMethodMapper.containsKey(requestCondition);
    }

    private HttpResponse handleGetLogin(final HttpRequest request) {
        final QueryParameters queryParameters = request.queryParameters();
        if (queryParameters.containsParam("account") && queryParameters.containsParam("password")) {
            final String account = queryParameters.getParameter("account");
            final String password = queryParameters.getParameter("password");

            final Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
            if (optionalUser.isPresent() && optionalUser.get().checkPassword(password)) {
                log.info(optionalUser.get().toString());
            }
        }

        return htmlHttpHandler.handle(
                "/login.html",
                request.protocolVersion()
        );
    }

    public static LoginHttpHandler getInstance() {
        return instance;
    }
}
