package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.catalina.auth.HttpCookie;
import org.apache.catalina.auth.Session;
import org.apache.catalina.auth.SessionManager;
import org.apache.catalina.io.FileReader;
import org.apache.catalina.request.HttpMethod;
import org.apache.catalina.request.Request;
import org.apache.catalina.request.RequestReader;
import org.apache.catalina.response.HttpStatus;
import org.apache.catalina.response.Response;
import org.apache.catalina.response.ResponsePage;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_PAGE_CONTENT = "Hello world!";
    private static final String ROOT_PATH = "/";
    private static final String LOGIN_PATH = "/login";
    private static final String REGISTER_PATH = "/register";
    private static final String NOT_FOUND_PAGE = "/404.html";
    private static final String BAD_REQUEST_PAGE = "/400.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String INDEX_PAGE = "/index.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

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
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             final OutputStream outputStream = connection.getOutputStream()) {

            Request request = RequestReader.readHeaders(reader);
            String response = handleRequest(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error("요청 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private String handleRequest(Request request) {
        if (request.isSameHttpMethod(HttpMethod.GET)) {
            return generateResponseForUrl(request).responseToString();
        }
        if (request.isSameHttpMethod(HttpMethod.POST)) {
            return generateResponseForPostUrl(request).responseToString();
        }
        log.warn("지원되지 않는 HTTP 메서드입니다.");
        return new Response(HttpStatus.BAD_REQUEST, "text/html", FileReader.loadFileContent(BAD_REQUEST_PAGE))
                .responseToString();
    }

    private Response generateResponseForUrl(Request request) {
        String accept = request.getFileType();
        if (ROOT_PATH.equals(request.getPathWithoutQuery())) {
            return new Response(HttpStatus.OK, accept, DEFAULT_PAGE_CONTENT);
        }
        if (!request.checkQueryParamIsEmpty()) {
            return generateResponseForQueryParam(request);
        }

        Optional<ResponsePage> responsePage = ResponsePage.fromUrl(request.getPathWithoutQuery(), request.getCookie());
        if (responsePage.isPresent()) {
            ResponsePage page = responsePage.get();
            Response response = new Response(page.getStatus(), accept, FileReader.loadFileContent(page.getFileName()));
            response.addLocation(page.getFileName());
            return response;
        }
        return new Response(HttpStatus.OK, accept, FileReader.loadFileContent(request.getPathWithoutQuery()));
    }

    private Response generateResponseForQueryParam(Request headers) {
        if (LOGIN_PATH.equals(headers.getPathWithoutQuery())) {
            return handleLoginRequest(headers);
        }
        throw new RuntimeException("'" + headers.getPathWithoutQuery() + "'는 정의되지 않은 URL입니다.");
    }

    private Response handleLoginRequest(Request request) {
        String accept = request.getFileType();
        Map<String, String> queryParams = request.getQueryParam();
        if (isMissingRequiredParams(request, queryParams)) {
            return new Response(HttpStatus.BAD_REQUEST, accept, FileReader.loadFileContent(BAD_REQUEST_PAGE));
        }

        Optional<User> user = authenticateUser(queryParams.get(ACCOUNT), queryParams.get(PASSWORD));
        if (user.isPresent()) {
            Session session = new Session(UUID.randomUUID().toString());
            session.setAttribute(session.getId(), user);
            SessionManager.getInstance().add(session);
            HttpCookie httpCookie = request.getCookie();
            String cookie = httpCookie.getCookies(session.getId());

            Response response
                    = new Response(HttpStatus.FOUND, accept, FileReader.loadFileContent(INDEX_PAGE));
            response.addHeader("Set-Cookie", cookie);
            response.addLocation(INDEX_PAGE);
            return response;
        }
        return new Response(HttpStatus.UNAUTHORIZED, accept, FileReader.loadFileContent(UNAUTHORIZED_PAGE));
    }

    private boolean isMissingRequiredParams(Request request, Map<String, String> queryParams) {
        return request.getQueryParam().size() < 2 ||
                (queryParams.get(ACCOUNT) == null && queryParams.get(PASSWORD) == null);
    }

    private Optional<User> authenticateUser(String account, String password) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            log.info("인증된 사용자: {}", user.get());
            return user;
        }
        return Optional.empty();
    }

    private Response generateResponseForPostUrl(Request headers) {
        String url = headers.getPathWithoutQuery();
        String accept = headers.getFileType();
        if (REGISTER_PATH.equals(url)) {
            return handleRegistration(headers.getBody(), accept);
        }
        return new Response(HttpStatus.BAD_REQUEST, accept, FileReader.loadFileContent(NOT_FOUND_PAGE));
    }

    private Response handleRegistration(Map<String, String> bodyParams, String accept) {
        String account = bodyParams.get(ACCOUNT);
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return new Response(HttpStatus.BAD_REQUEST, accept, FileReader.loadFileContent(BAD_REQUEST_PAGE));
        }
        String password = bodyParams.get(PASSWORD);
        String email = bodyParams.get(EMAIL);
        InMemoryUserRepository.save(new User(account, password, email));
        Response response
                = new Response(HttpStatus.FOUND, accept, FileReader.loadFileContent(INDEX_PAGE));
        response.addLocation(INDEX_PAGE);
        return response;
    }
}
