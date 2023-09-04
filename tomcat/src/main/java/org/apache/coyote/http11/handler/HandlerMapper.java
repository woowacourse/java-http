package org.apache.coyote.http11.handler;

import static org.reflections.Reflections.log;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Request;
import org.apache.coyote.http11.RequestLine;
import org.apache.coyote.http11.Response;

public class HandlerMapper {
    private static Map<HandlerStatus, Function<Request, Response>> HANDLERS = new HashMap<>();

    public HandlerMapper() {
        init();
    }

    public void init() {
        HANDLERS.put(new HandlerStatus("GET", "/"), this::rootHandler);
        HANDLERS.put(new HandlerStatus("GET", "/login"), this::loginHandler);
        HANDLERS.put(new HandlerStatus("GET", "/login", Set.of("account", "password")),
                this::loginWithQueryParameterHandler);
        HANDLERS.put(new HandlerStatus("POST", "/login"), this::loginFormHandler);
        HANDLERS.put(new HandlerStatus("GET", "/register"), this::registerHandler);
        HANDLERS.put(new HandlerStatus("POST", "/register"), this::registerFormHandler);
    }

    public Response rootHandler(final Request request) {
        final var responseBody = "Hello world!";

        final String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        return new Response(response);
    }

    public Response htmlHandler(final Request request) {
        final var responseBody = request.getRequestLine().readFile();

        final String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        return new Response(response);
    }


    public Response cssHandler(final Request request) {
        final var responseBody = request.getRequestLine().readFile();

        final String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/css;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        return new Response(response);
    }

    public Response jsHandler(final Request request) {
        final var responseBody = request.getRequestLine().readFile();

        final String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/javascript;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        return new Response(response);
    }

    public Response loginHandler(final Request request) {
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final String responseBody;
        try {
            responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        } catch (IOException e) {
            throw new IllegalArgumentException("login.html이 존재하지 않습니다.");
        }

        // request header의 cookie에 세션아이디가 없으면 response에 set-cookie추가
        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        return new Response(response);
    }

    public Response loginWithQueryParameterHandler(final Request request) {
        final RequestLine requestLine = request.getRequestLine();
        final var queryParameter = requestLine.getRequestURI().getQueryParameter();

        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final String responseBody;
        try {
            responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        } catch (IOException e) {
            throw new IllegalArgumentException("login.html이 존재하지 않습니다.");
        }

        final User user = InMemoryUserRepository.findByAccount(queryParameter.get("account"))
                .orElseGet(null);

        if (user != null) {
            if (!user.checkPassword(queryParameter.get("password"))) {
                log.error("유저의 아이디와 비밀번호가 일치하지않습니다.");
            } else {
                log.info("user : " + user);
            }
        }

        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        return new Response(response);
    }

    public Response loginFormHandler(final Request request) {
        final Map<String, String> requestForms = request.getRequestForms().getRequestForms();
        Optional<User> user = login(requestForms.get("account"), requestForms.get("password"));
        if (user.isPresent()) {
            return loginSuccess();
        }
        return loginFail();
    }

    private Response loginSuccess() {
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String responseBody;
        try {
            responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        } catch (IOException e) {
            throw new IllegalArgumentException("index.html이 존재하지 않습니다.");
        }

        // request header의 cookie에 세션아이디가 없으면 response에 set-cookie추가
        final var response = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        return new Response(response);
    }

    private Response loginFail() {
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        final String responseBody;
        try {
            responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        } catch (IOException e) {
            throw new IllegalArgumentException("index.html이 존재하지 않습니다.");
        }

        final var response = String.join("\r\n",
                "HTTP/1.1 401 Unauthorized ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        return new Response(response);
    }

    private Optional<User> login(final String account, final String password) {
        return InMemoryUserRepository.findByAccountAndPassword(account, password);
    }

    public Response registerHandler(final Request request) {
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        final String responseBody;
        try {
            responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        } catch (IOException e) {
            throw new IllegalArgumentException("register.html이 존재하지 않습니다.");
        }

        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        return new Response(response);
    }

    public Response registerFormHandler(final Request request) {
        final Map<String, String> requestForms = request.getRequestForms().getRequestForms();
        final String account = requestForms.get("account");
        final String email = requestForms.get("email");
        final String password = requestForms.get("password");
        InMemoryUserRepository.save(new User(account, password, email));

        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String responseBody;
        try {
            responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        } catch (IOException e) {
            throw new IllegalArgumentException("index.html이 존재하지 않습니다.");
        }

        // request header의 cookie에 세션아이디가 없으면 response에 set-cookie추가
        final var response = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        return new Response(response);
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

        if (requestLine.getPath().endsWith(".html")) {
            return htmlHandler(request);
        } else if (requestLine.getPath().endsWith(".css")) {
            return cssHandler(request);
        } else if (requestLine.getPath().endsWith(".js")) {
            return jsHandler(request);
        }
        throw new IllegalArgumentException("매핑되는 핸들러가 존재하지 않습니다.");
    }
}
