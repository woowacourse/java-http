package org.apache.coyote.http11.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.Response;

public class HandlerMapper {
    private static final Map<HandlerStatus, Function<Request, Response>> HANDLERS = new HashMap<>();

    public HandlerMapper() {
        init();
    }

    public void init() {
        HANDLERS.put(new HandlerStatus("GET", "/"), this::rootHandler);
        HANDLERS.put(new HandlerStatus("GET", "/login"), this::loginHandler);
        HANDLERS.put(new HandlerStatus("POST", "/login"), this::loginFormHandler);
        HANDLERS.put(new HandlerStatus("GET", "/register"), this::registerHandler);
        HANDLERS.put(new HandlerStatus("POST", "/register"), this::registerFormHandler);
    }

    public Response rootHandler(final Request request) {
        return Response.createByResponseBody(HttpStatus.OK, "Hello world!");
    }

    public Response loginHandler(final Request request) {
        if (request.getSessionValue("user") != Optional.empty()) {
            return Response.createByTemplate(HttpStatus.FOUND, "index.html");
        }

        return Response.createByTemplate(HttpStatus.OK, "login.html");
    }

    public Response loginFormHandler(final Request request) {
        final Map<String, String> requestForms = request.getRequestForms().getFormData();
        final Optional<User> user = login(requestForms.get("account"), requestForms.get("password"));
        return user.map(value -> loginSuccess(request, value)).orElseGet(this::loginFail);
    }

    private Response loginSuccess(final Request request, final User user) {
        if (request.noSession()) {
            final Session session = new Session();
            session.setAttribute("user", user);
            SessionManager.add(session);

            final Map<String, String> header = new HashMap<>();
            header.put("Set-Cookie", "JSESSIONID=" + session.getId());
            return Response.createByTemplate(HttpStatus.FOUND, "index.html", header);
        }
        return Response.createByTemplate(HttpStatus.FOUND, "index.html");
    }

    private Response loginFail() {
        return Response.createByTemplate(HttpStatus.UNAUTHORIZED, "401.html");
    }

    private Optional<User> login(final String account, final String password) {
        return InMemoryUserRepository.findByAccountAndPassword(account, password);
    }

    public Response registerHandler(final Request request) {
        return Response.createByTemplate(HttpStatus.OK, "register.html");
    }

    public Response registerFormHandler(final Request request) {
        final Map<String, String> requestForms = request.getRequestForms().getFormData();
        final String account = requestForms.get("account");
        final String email = requestForms.get("email");
        final String password = requestForms.get("password");
        InMemoryUserRepository.save(new User(account, password, email));

        return Response.createByTemplate(HttpStatus.FOUND, "index.html");
    }

    public Response handle(final Request request) {
        final RequestLine requestLine = request.getRequestLine();
        final String path = requestLine.getPath();
        final String httpMethod = requestLine.getHttpMethod();
        final Map<String, String> queryParameter = requestLine.getRequestURI().getQueryParameter();
        final Set<String> queryParameterKeys = queryParameter.keySet();
        final HandlerStatus handlerStatus = new HandlerStatus(httpMethod, path, queryParameterKeys);

        final Function<Request, Response> handler = HANDLERS.get(handlerStatus);
        if (handler != null) {
            return handler.apply(request);
        }

        if (requestLine.getRequestURI().isExistFile()) {
            return Response.createByTemplate(request.getRequestLine().getRequestURI());
        }
        throw new IllegalArgumentException("매핑되는 핸들러가 존재하지 않습니다.");
    }
}
