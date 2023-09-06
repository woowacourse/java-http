package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.*;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.session.JSessionIdGenerator;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.NoSuchElementException;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String LOGIN_PAGE_URI = "/login.html";
    private static final String REGISTER_PAGE_URI = "/register.html";
    private static final String UNAUTHORIZED_PAGE_URI = "/401.html";
    private static final String INDEX_PAGE_URI = "/index.html";

    private final SessionManager sessionManager = new SessionManager();
    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            HttpRequest httpRequest = HttpRequest.from(bufferedReader);

            HttpRequestStartLine httpRequestStartLine = httpRequest.getHttpRequestStartLine();
            HttpRequestHeader httpRequestHeader = httpRequest.getHttpRequestHeader();
            HttpRequestBody httpRequestBody = httpRequest.getHttpRequestBody();

            ResponseEntity responseEntity = createResponse(httpRequestStartLine, httpRequestBody);
            String response = HttpResponse.from(responseEntity).getResponse();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private ResponseEntity createResponse(
            HttpRequestStartLine httpRequestStartLine,
            HttpRequestBody httpRequestBody
    ) throws IOException {
        String requestURI = httpRequestStartLine.getRequestURI();
        log.info("REQUEST URI: {}", requestURI);

        if (requestURI.equals("/")) {
            return findMainPageResource(requestURI);
        }

        if (requestURI.startsWith("/login")) {
            return login(httpRequestStartLine, httpRequestBody);
        }

        if (requestURI.startsWith("/register")) {
            return register(httpRequestStartLine, httpRequestBody);
        }

        return findStaticResource(requestURI);
    }

    private ResponseEntity findMainPageResource(String requestURI) {
        final var responseBody = "Hello world!";
        return ResponseEntity
                .builder()
                .httpStatus(HttpStatus.OK)
                .requestURI(requestURI)
                .responseBody(responseBody)
                .build();
    }

    private ResponseEntity findStaticResource(String requestURI) throws IOException {
        URL resource = getClass()
                .getClassLoader()
                .getResource("static" + requestURI);
        File file = new File(resource.getFile());
        String responseBody = new String(Files.readAllBytes(file.toPath()));
        return ResponseEntity
                .builder()
                .httpStatus(HttpStatus.OK)
                .requestURI(requestURI)
                .responseBody(responseBody)
                .build();
    }

    private ResponseEntity login(
            HttpRequestStartLine httpRequestStartLine,
            HttpRequestBody httpRequestBody
    ) {
        HttpMethod httpMethod = httpRequestStartLine.getHttpMethod();
        String requestURI = httpRequestStartLine.getRequestURI();
        String account = httpRequestBody.find("account");

        if (httpMethod == HttpMethod.GET && account == null) {
            return ResponseEntity.builder()
                    .httpStatus(HttpStatus.OK)
                    .requestURI(requestURI)
                    .location(LOGIN_PAGE_URI)
                    .build();
        }

        String password = httpRequestBody.find("password");

        User findAccount = findAccount(account);
        boolean isCorrectPassword = findAccount.checkPassword(password);

        if (!isCorrectPassword) {
            return handleLoginFail(requestURI, findAccount);
        }

        return handleLoginSuccess(requestURI, findAccount);
    }

    private ResponseEntity handleLoginFail(String requestURI, User findAccount) {
        log.info("account {} 비밀번호 불일치로 로그인 실패", findAccount.getAccount());
        return ResponseEntity
                .builder()
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .requestURI(requestURI)
                .location(UNAUTHORIZED_PAGE_URI)
                .build();
    }

    private ResponseEntity handleLoginSuccess(String requestURI, User findAccount) {
        log.info("account {} 로그인 성공", findAccount.getAccount());
        ResponseEntity responseEntity = ResponseEntity
                .builder()
                .httpStatus(HttpStatus.FOUND)
                .requestURI(requestURI)
                .location(INDEX_PAGE_URI)
                .build();
        String jSessionId = JSessionIdGenerator.generateRandomSessionId();
        responseEntity.setCookie("JSESSIONID", jSessionId);
        Session session = new Session(jSessionId);
        sessionManager.add(session);
        return responseEntity;
    }

    private User findAccount(String account) {
        Optional<User> findAccount = InMemoryUserRepository.findByAccount(account);
        if (findAccount.isEmpty()) {
            log.info("해당하는 Account를 찾을 수 없음! account={}", account);
            throw new NoSuchElementException();
        }

        return findAccount.get();
    }

    private ResponseEntity register(
            HttpRequestStartLine httpRequestStartLine,
            HttpRequestBody httpRequestBody
    ) {
        HttpMethod httpMethod = httpRequestStartLine.getHttpMethod();
        String requestURI = httpRequestStartLine.getRequestURI();

        if (httpMethod == HttpMethod.GET) {
            return ResponseEntity.builder()
                    .httpStatus(HttpStatus.OK)
                    .requestURI(requestURI)
                    .location(REGISTER_PAGE_URI)
                    .build();
        }

        String account = httpRequestBody.find("account");
        String password = httpRequestBody.find("password");
        String email = httpRequestBody.find("email");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        log.info("{} user 회원가입 성공", account);
        return ResponseEntity
                .builder()
                .httpStatus(HttpStatus.FOUND)
                .requestURI(requestURI)
                .location(INDEX_PAGE_URI)
                .build();
    }
}
