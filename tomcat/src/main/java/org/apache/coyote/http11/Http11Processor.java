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
import org.apache.coyote.Processor;
import org.apache.catalina.request.Request;
import org.apache.catalina.request.RequestReader;
import org.apache.catalina.response.HttpStatus;
import org.apache.catalina.response.ResponseContent;
import org.apache.catalina.response.ResponsePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
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
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            Request request = RequestReader.readHeaders(reader);
            HttpCookie.setCookies(request.getCookie());
            handleRequestMethod(request);
        } catch (IOException e) {
            log.error("요청 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private void handleRequestMethod(Request request) {
        String httpMethod = request.getHttpMethod();
        if (httpMethod.equals("GET")) {
            handleGetRequest(request);
        } else if (httpMethod.equals("POST")) {
            handlePostRequest(request);
        } else {
            log.warn("지원되지 않는 HTTP 메서드: {}", httpMethod);
        }
    }

    private void handleGetRequest(Request headers) {
        try (final OutputStream outputStream = connection.getOutputStream()) {
            String response = generateResponseForUrl(headers).responseToString();
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error("GET 요청 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private ResponseContent generateResponseForUrl(Request headers) {
        String accept = headers.getFileType();
        if (ROOT_PATH.equals(headers.getUrl())) {
            return new ResponseContent(HttpStatus.OK, accept, DEFAULT_PAGE_CONTENT);
        }
        if (!headers.checkQueryParamIsEmpty()) {
            return generateResponseForQueryParam(headers);
        }

        Optional<ResponsePage> responsePage = ResponsePage.fromUrl(headers.getUrl());
        if (responsePage.isPresent()) {
            ResponsePage page = responsePage.get();
            return new ResponseContent(page.getStatus(), accept, FileReader.loadFileContent(page.getFileName()));
        }
        return new ResponseContent(HttpStatus.OK, accept, FileReader.loadFileContent(headers.getUrl()));
    }

    private ResponseContent generateResponseForQueryParam(Request headers) {
        if (LOGIN_PATH.equals(headers.getUrl())) {
            return handleLoginRequest(headers);
        }
        throw new RuntimeException("'" + headers.getUrl() + "'는 정의되지 않은 URL입니다.");
    }

    private ResponseContent handleLoginRequest(Request request) {
        String accept = request.getFileType();
        Map<String, String> queryParams = request.getQueryParam();
        if (request.getQueryParam().size() < 2) {
            return new ResponseContent(HttpStatus.BAD_REQUEST, accept, FileReader.loadFileContent(BAD_REQUEST_PAGE));
        }
        if (queryParams.get(ACCOUNT) == null || queryParams.get(PASSWORD) == null) {
            return new ResponseContent(HttpStatus.BAD_REQUEST, accept, FileReader.loadFileContent(BAD_REQUEST_PAGE));
        }

        Optional<User> user = authenticateUser(queryParams.get(ACCOUNT), queryParams.get(PASSWORD));
        if (user.isPresent()) {
            Session session = new Session(UUID.randomUUID().toString());
            session.setAttribute("user", user);
            SessionManager.getInstance().add(session);
            String cookie = HttpCookie.ofJSessionId(session.getId());

            return new ResponseContent(HttpStatus.FOUND, accept, FileReader.loadFileContent(INDEX_PAGE), cookie);
        }
        return new ResponseContent(HttpStatus.UNAUTHORIZED, accept, FileReader.loadFileContent(UNAUTHORIZED_PAGE));
    }

    private Optional<User> authenticateUser(String account, String password) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            log.info("인증된 사용자: {}", user.get());
        }
        return user;
    }

    private void handlePostRequest(Request headers) {
        try (final OutputStream outputStream = connection.getOutputStream()) {
            String response = generateResponseForPostUrl(headers).responseToString();
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error("POST 요청 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private static ResponseContent generateResponseForPostUrl(Request headers) {
        String url = headers.getUrl();
        String accept = headers.getFileType();
        if (REGISTER_PATH.equals(url)) {
            return handleRegistration(headers.getBody(), accept);
        }
        return new ResponseContent(HttpStatus.BAD_REQUEST, accept, FileReader.loadFileContent(NOT_FOUND_PAGE));
    }

    private static ResponseContent handleRegistration(Map<String, String> bodyParams, String accept) {
        String account = bodyParams.get(ACCOUNT);
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return new ResponseContent(HttpStatus.BAD_REQUEST, accept, FileReader.loadFileContent(BAD_REQUEST_PAGE));
        }
        String password = bodyParams.get(PASSWORD);
        String email = bodyParams.get(EMAIL);
        InMemoryUserRepository.save(new User(account, email, password));
        return new ResponseContent(HttpStatus.CREATED, accept, FileReader.loadFileContent(INDEX_PAGE));
    }
}
