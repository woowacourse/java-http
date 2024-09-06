package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Optional;

import org.apache.catalina.io.FileReader;
import org.apache.catalina.request.RequestReader;
import org.apache.catalina.response.HttpStatus;
import org.apache.catalina.response.ResponseContent;
import org.apache.catalina.response.ResponsePage;
import org.apache.coyote.Processor;
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

    private static final String HTTP_METHOD = "HttpMethod";
    private static final String URL = "Url";
    private static final String ACCEPT = "Accept";
    private static final String IS_QUERY_PARAM = "IsQueryParam";
    private static final String QUERY_PARAM_SIZE = "QueryParamSize";
    private static final String CONTENT_LENGTH = "Content-Length";

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
            Map<String, String> headers = RequestReader.readHeaders(reader);
            handleRequestMethod(reader, headers);
        } catch (IOException e) {
            log.error("요청 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private void handleRequestMethod(BufferedReader reader, Map<String, String> headers) {
        String httpMethod = headers.get(HTTP_METHOD);
        if ("GET".equalsIgnoreCase(httpMethod)) {
            handleGetRequest(headers);
        } else if ("POST".equalsIgnoreCase(httpMethod)) {
            handlePostRequest(reader, headers);
        } else {
            log.warn("지원되지 않는 HTTP 메서드: {}", httpMethod);
        }
    }

    private void handleGetRequest(Map<String, String> headers) {
        try (final OutputStream outputStream = connection.getOutputStream()) {
            String response = generateResponseForUrl(headers).responseToString();
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error("GET 요청 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private ResponseContent generateResponseForUrl(Map<String, String> headers) {
        String url = headers.get(URL);
        String accept = headers.get(ACCEPT);
        if (ROOT_PATH.equals(url)) {
            return new ResponseContent(HttpStatus.OK, accept, DEFAULT_PAGE_CONTENT);
        }
        if (Boolean.parseBoolean(headers.get(IS_QUERY_PARAM))) {
            return generateResponseForQueryParam(headers);
        }

        Optional<ResponsePage> responsePage = ResponsePage.fromUrl(url);
        if (responsePage.isPresent()) {
            ResponsePage page = responsePage.get();
            return new ResponseContent(page.getStatus(), accept, FileReader.loadFileContent(page.getFileName()));
        }

        return new ResponseContent(HttpStatus.OK, accept, FileReader.loadFileContent(url));
    }

    private ResponseContent generateResponseForQueryParam(Map<String, String> headers) {
        String url = headers.get(URL);
        if (LOGIN_PATH.equals(url)) {
            return handleLoginRequest(headers);
        }
        throw new RuntimeException("'" + url + "'는 정의되지 않은 URL입니다.");
    }

    private ResponseContent handleLoginRequest(Map<String, String> queryParams) {
        String accept = queryParams.get(ACCEPT);
        if (Integer.parseInt(queryParams.get(QUERY_PARAM_SIZE)) < 2) {
            return new ResponseContent(HttpStatus.BAD_REQUEST, accept, FileReader.loadFileContent(BAD_REQUEST_PAGE));
        }

        String account = queryParams.get(ACCOUNT);
        String password = queryParams.get(PASSWORD);
        if (account == null || password == null) {
            return new ResponseContent(HttpStatus.BAD_REQUEST, accept, FileReader.loadFileContent(BAD_REQUEST_PAGE));
        }
        if (authenticateUser(account, password)) {
            return new ResponseContent(HttpStatus.FOUND, accept, FileReader.loadFileContent(INDEX_PAGE));
        }
        return new ResponseContent(HttpStatus.UNAUTHORIZED, accept, FileReader.loadFileContent(UNAUTHORIZED_PAGE));
    }

    private boolean authenticateUser(String account, String password) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            log.info("인증된 사용자: {}", user.get());
            return true;
        }
        return false;
    }

    private void handlePostRequest(BufferedReader reader, Map<String, String> headers) {
        int contentLength = Integer.parseInt(headers.get(CONTENT_LENGTH));
        Map<String, String> bodyParams = RequestReader.readBody(reader, contentLength);

        try (final OutputStream outputStream = connection.getOutputStream()) {
            String response = generateResponseForUrl(headers, bodyParams).responseToString();
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error("POST 요청 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private static ResponseContent generateResponseForUrl(Map<String, String> headers, Map<String, String> bodyParams) {
        String url = headers.get(URL);
        String accept = headers.get(ACCEPT);
        if (REGISTER_PATH.equals(url)) {
            return handleRegistration(bodyParams, accept);
        }
        return new ResponseContent(HttpStatus.BAD_REQUEST, accept, FileReader.loadFileContent(NOT_FOUND_PAGE));
    }

    private static ResponseContent handleRegistration(Map<String, String> bodyParams, String accept) {
        if (InMemoryUserRepository.findByAccount(bodyParams.get(ACCOUNT)).isPresent()) {
            return new ResponseContent(HttpStatus.BAD_REQUEST, accept, FileReader.loadFileContent(BAD_REQUEST_PAGE));
        }
        InMemoryUserRepository.save(new User(bodyParams.get(ACCOUNT), bodyParams.get(EMAIL), bodyParams.get(PASSWORD)));
        return new ResponseContent(HttpStatus.CREATED, accept, FileReader.loadFileContent(INDEX_PAGE));
    }
}
