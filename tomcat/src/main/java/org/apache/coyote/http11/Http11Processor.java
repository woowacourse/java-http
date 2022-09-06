package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.constant.HttpMethod;
import org.apache.coyote.http11.constant.HttpStatus;
import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestAssembler;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String SESSION_COOKIE_NAME = "JSESSIONID";
    private static final String RESOURCE_FOLDER = "static";

    private final Socket connection;
    private final HttpRequestAssembler requestAssembler;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        requestAssembler = new HttpRequestAssembler();
    }

    @Override
    public void run() {
        process(connection);
    }

    public HttpResponse get(HttpRequest request, OutputStream outputStream) throws IOException {
        HttpResponse response = new HttpResponse();
        if (request.isResource()) {
            response.loadResource(request.getUrl());
            return response;
        }
        if (request.compareUrl("/login")) {
            if (request.getCookies().getCookie("JSESSIONID") != null) {
                response.statusCode(HttpStatus.REDIRECT);
                response.addHeader("Location", "/index.html");
                return response;
            }

            response.loadResource("/index.html");
            return response;
        }
        if (request.compareUrl("/register")) {
            response.loadResource("/register.html");
            return response;
        }
        response.loadRawString("Hello world!");
        return response;
    }

    public HttpResponse post(HttpRequest request, OutputStream outputStream) throws IOException {
        Map<String, String> bodyParams = request.getBody();
        HttpResponse httpResponse = new HttpResponse();
        if (request.compareUrl("/register")) {
            String account = Objects.requireNonNull(bodyParams.get("account"), "계정이 입력되지 않았습니다.");
            String password = Objects.requireNonNull(bodyParams.get("password"), "비밀번호가 입력되지 않았습니다.");
            String email = Objects.requireNonNull(bodyParams.get("email"), "이메일이 입력되지 않았습니다.");

            User user = new User(account, password, email);
            InMemoryUserRepository.save(user);

            httpResponse.addHeader("Location", "/index.html");
            httpResponse.statusCode(HttpStatus.REDIRECT);

            return httpResponse;
        }

        if (request.compareUrl("/login")) {
            String account = Objects.requireNonNull(bodyParams.get("account"), "계정이 입력되지 않았습니다.");
            String password = Objects.requireNonNull(bodyParams.get("password"), "비밀번호가 입력되지 않았습니다.");

            Optional<User> userByAccount = InMemoryUserRepository.findByAccount(account)
                    .filter(user -> user.checkPassword(password));
            Cookie cookie = request.getCookies();

            if (userByAccount.isPresent()) {
                log.info("로그인 성공!" + " 아이디: " + userByAccount.get().getAccount());
                if (!cookie.hasCookie(SESSION_COOKIE_NAME)) {
                    String jSessionId = UUID.randomUUID().toString();
                    httpResponse.addCookie(SESSION_COOKIE_NAME, jSessionId);
                    httpResponse.addHeader("Location", "/index.html");
                    Session.put(jSessionId, userByAccount.get());

                    return httpResponse;
                }
            }

            httpResponse.loadResource("/401.html");
            return httpResponse;
        }

        httpResponse.loadResource("/404.html");
        return httpResponse;
    }

    @Override
    public void process(final Socket connection) {
        try (
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                OutputStream outputStream = connection.getOutputStream();
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
        ) {
            HttpRequest request = requestAssembler.makeRequest(bufferedReader);
            execute(outputStream, request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void execute(OutputStream outputStream, HttpRequest request) throws IOException {
        HttpResponse response = processByMethod(outputStream, request);

        outputStream.write(response.toMessage().getBytes());
        outputStream.flush();
    }

    private HttpResponse processByMethod(OutputStream outputStream, HttpRequest request) throws IOException {
        if (request.getMethod() == HttpMethod.GET) {
            return get(request, outputStream);
        }

        if (request.getMethod() == HttpMethod.POST) {
            return post(request, outputStream);
        }

        throw new IllegalArgumentException("지원하지 않는 Http Method입니다.");
    }
}
