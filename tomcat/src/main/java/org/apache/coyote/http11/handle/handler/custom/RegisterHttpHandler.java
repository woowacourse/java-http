package org.apache.coyote.http11.handle.handler.custom;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.handle.HttpHandlerCondition;
import org.apache.coyote.http11.handle.handler.HttpHandler;
import org.apache.coyote.http11.handle.handler.MultiConditionHandler;
import org.apache.coyote.http11.handle.handler.resource.HtmlHttpHandler;
import org.apache.coyote.http11.reqeust.HttpMethod;
import org.apache.coyote.http11.reqeust.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class RegisterHttpHandler extends MultiConditionHandler {

    private static final RegisterHttpHandler instance = new RegisterHttpHandler();

    private final HtmlHttpHandler htmlHttpHandler = HtmlHttpHandler.getInstance();

    private final Map<HttpHandlerCondition, Function<HttpRequest, HttpResponse>> handlerMethodMapper = Map.of(
            new HttpHandlerCondition(HttpMethod.GET, "/register"), this::handleGetRegister,
            new HttpHandlerCondition(HttpMethod.POST, "/register"), this::handlePostRegister
    );

    private RegisterHttpHandler() {
    }

    @Override
    protected Map<HttpHandlerCondition, Function<HttpRequest, HttpResponse>> getHandlerMethodMapper() {
        return handlerMethodMapper;
    }

    private HttpResponse handleGetRegister(final HttpRequest request) {
        return htmlHttpHandler.handle(
                "/register.html",
                request.protocolVersion(),
                HttpStatus.OK
        );
    }

    private HttpResponse handlePostRegister(final HttpRequest request) {
        final Map<String, String> bodyParameters = parseBodyByFormUrlEncoded(request.body());
        final String account = bodyParameters.get("account");
        final String password = bodyParameters.get("password");
        final String email = bodyParameters.get("email");
        if (account == null || password == null || email == null) {
            throw new IllegalArgumentException("잘못된 회원가입 바디 파라미터입니다. " + request.body());
        }
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다. " + account);
        }
        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        final HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.addHeader("Location", "/index.html");

        return new HttpResponse(
                request.protocolVersion(),
                HttpStatus.SEE_OTHER,
                responseHeaders
        );
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

    public static HttpHandler getInstance() {
        return instance;
    }
}
