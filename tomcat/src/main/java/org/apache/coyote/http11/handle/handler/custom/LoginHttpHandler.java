package org.apache.coyote.http11.handle.handler.custom;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.handle.HttpHandlerCondition;
import org.apache.coyote.http11.handle.handler.MultiConditionHandler;
import org.apache.coyote.http11.handle.handler.resource.HtmlHttpHandler;
import org.apache.coyote.http11.reqeust.HttpMethod;
import org.apache.coyote.http11.reqeust.HttpRequest;
import org.apache.coyote.http11.reqeust.QueryParameters;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHttpHandler extends MultiConditionHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginHttpHandler.class);

    private static final LoginHttpHandler instance = new LoginHttpHandler();

    private final HtmlHttpHandler htmlHttpHandler = HtmlHttpHandler.getInstance();

    private final Map<HttpHandlerCondition, Function<HttpRequest, HttpResponse>> handlerMethodMapper = Map.of(
            new HttpHandlerCondition(HttpMethod.GET, "/login"), this::handleGetLogin
    );

    private LoginHttpHandler() {
    }

    @Override
    protected Map<HttpHandlerCondition, Function<HttpRequest, HttpResponse>> getHandlerMethodMapper() {
        return handlerMethodMapper;
    }

    private HttpResponse handleGetLogin(final HttpRequest request) {
        final QueryParameters queryParameters = request.queryParameters();
        if (hasLoginQueryParameters(queryParameters)) {
            return handleLogin(request, queryParameters);
        }

        return htmlHttpHandler.handle(
                "/login.html",
                request.protocolVersion(),
                HttpStatus.OK
        );
    }

    private boolean hasLoginQueryParameters(final QueryParameters queryParameters) {
        return queryParameters.containsParam("account") && queryParameters.containsParam("password");
    }

    private HttpResponse handleLogin(
            final HttpRequest request,
            final QueryParameters queryParameters
    ) {
        final String account = queryParameters.getParameter("account");
        final String password = queryParameters.getParameter("password");
        final HttpHeaders responseHeaders = new HttpHeaders();

        if (checkAuthorization(account, password)) {
            responseHeaders.addHeader("Location", "/index.html");
            return new HttpResponse(
                    request.protocolVersion(),
                    HttpStatus.SEE_OTHER,
                    responseHeaders
            );
        }

        responseHeaders.addHeader("Location", "/401.html");
        return new HttpResponse(
                request.protocolVersion(),
                HttpStatus.SEE_OTHER,
                responseHeaders
        );
    }

    private boolean checkAuthorization(
            final String account,
            final String password
    ) {
        final Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);

        return optionalUser.isPresent() && optionalUser.get().checkPassword(password);
    }

    public static LoginHttpHandler getInstance() {
        return instance;
    }
}
