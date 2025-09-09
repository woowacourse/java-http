package org.apache.coyote.http11.handle.handler.custom;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpSession;
import org.apache.coyote.http11.handle.HttpHandlerCondition;
import org.apache.coyote.http11.handle.handler.MultiConditionHandler;
import org.apache.coyote.http11.handle.handler.resource.HtmlHttpHandler;
import org.apache.coyote.http11.reqeust.HttpMethod;
import org.apache.coyote.http11.reqeust.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHttpHandler extends MultiConditionHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginHttpHandler.class);

    private static final LoginHttpHandler instance = new LoginHttpHandler();

    private final HtmlHttpHandler htmlHttpHandler = HtmlHttpHandler.getInstance();

    private final Map<HttpHandlerCondition, Function<HttpRequest, HttpResponse>> handlerMethodMapper = Map.of(
            new HttpHandlerCondition(HttpMethod.GET, "/login"), this::handleGetLogin,
            new HttpHandlerCondition(HttpMethod.POST, "/login"), this::handlePostLogin
    );

    private LoginHttpHandler() {
    }

    @Override
    protected Map<HttpHandlerCondition, Function<HttpRequest, HttpResponse>> getHandlerMethodMapper() {
        return handlerMethodMapper;
    }

    private HttpResponse handleGetLogin(final HttpRequest request) {
        if (isAlreadyLoginSession(request)) {
            final HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.addHeader("Location", "/index.html");

            return new HttpResponse(
                    request.protocolVersion(),
                    HttpStatus.SEE_OTHER,
                    responseHeaders
            );
        }

        return htmlHttpHandler.handle(
                "/login.html",
                request.protocolVersion(),
                HttpStatus.OK
        );
    }

    private boolean isAlreadyLoginSession(final HttpRequest request) {
        final HttpCookie sessionCookie = request.headers().getCookies().getCookie(HttpSession.SESSION_TYPE);
        if (sessionCookie != null) {
            final String sessionId = sessionCookie.value();
            final HttpSession session = SessionManager.getInstance().findSession(sessionId);
            if (session != null) {
                final Object user = session.getValue("user");
                if (user instanceof User) {
                    log.info("로그인 된 유저입니다. {}", user);

                    return true;
                }
            }
        }

        return false;
    }

    private HttpResponse handlePostLogin(final HttpRequest request) {
        final Map<String, String> bodyParameters = parseBodyByFormUrlEncoded(request.body());

        return handleLogin(request, bodyParameters);
    }


    private HttpResponse handleLogin(
            final HttpRequest request,
            final Map<String, String> bodyParameters
    ) {
        final String account = bodyParameters.get("account");
        final String password = bodyParameters.get("password");
        if (account == null || password == null) {
            throw new IllegalArgumentException("잘못된 로그인 바디 파라미터입니다. " + request.body());
        }

        final HttpHeaders responseHeaders = new HttpHeaders();

        final Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        if (optionalUser.isPresent() && optionalUser.get().checkPassword(password)) {
            responseHeaders.addHeader("Location", "/index.html");
            setSession(responseHeaders, optionalUser.get());
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

    private void setSession(
            final HttpHeaders responseHeaders,
            final User user
    ) {
        final HttpSession session = new HttpSession();
        session.setAttribute("user", user);
        SessionManager.getInstance().add(session);
        responseHeaders.setSessionId(session);
    }

    private Map<String, String> parseBodyByFormUrlEncoded(final String body) {
        if (body == null || body.isBlank()) {
            return Map.of();
        }
        final Map<String, String> bodyParameters = new HashMap<>();
        final String[] keyValues = body.split("&");
        for (final String keyValue : keyValues) {
            final String[] split = keyValue.split("=", 2);
            final String key = split[0];
            final String value = split[1];
            bodyParameters.put(key, value);
        }

        return bodyParameters;
    }

    public static LoginHttpHandler getInstance() {
        return instance;
    }
}
