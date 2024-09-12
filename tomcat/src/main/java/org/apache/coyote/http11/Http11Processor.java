package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.catalina.auth.HttpCookie;
import org.apache.catalina.auth.Session;
import org.apache.catalina.auth.SessionManager;
import org.apache.catalina.http.VersionOfProtocol;
import org.apache.catalina.io.FileReader;
import org.apache.catalina.io.RequestParser;
import org.apache.catalina.io.RequestReader;
import org.apache.catalina.request.HttpMethod;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.request.RequestBody;
import org.apache.catalina.request.RequestHeader;
import org.apache.catalina.request.RequestLine;
import org.apache.catalina.response.HttpResponse;
import org.apache.catalina.response.HttpStatus;
import org.apache.catalina.response.ResponsePage;
import org.apache.catalina.response.StatusLine;
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

            HttpRequest httpRequest = readAndParserRequest(reader);

            HttpResponse response = handleRequest(httpRequest);

            outputStream.write(response.toString().getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error("요청 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private HttpRequest readAndParserRequest(BufferedReader reader) {
        List<String> request = RequestReader.readRequest(reader);
        RequestLine requestLine = new RequestLine(request.getFirst());
        RequestHeader requestHeader = new RequestHeader(RequestParser.parseHeaders(request));
        String body = RequestReader.readBody(reader, requestHeader.getContentLength());
        RequestBody requestBody = new RequestBody(RequestParser.parseParamValues(body));
        return new HttpRequest(requestLine, requestHeader, requestBody);
    }

    private HttpResponse handleRequest(HttpRequest httpRequest) {
        if (httpRequest.isSameHttpMethod(HttpMethod.GET)) {
            return generateResponseForUrl(httpRequest);
        }
        if (httpRequest.isSameHttpMethod(HttpMethod.POST)) {
            return generateResponseForPostUrl(httpRequest);
        }
        log.warn("지원되지 않는 HTTP 메서드입니다.");
        return new HttpResponse(
                new StatusLine(httpRequest.getVersionOfProtocol(), HttpStatus.BAD_REQUEST),
                "text/html",
                FileReader.loadFileContent(BAD_REQUEST_PAGE));
    }

    private HttpResponse generateResponseForUrl(HttpRequest httpRequest) {
        String accept = httpRequest.getFileType();
        if (ROOT_PATH.equals(httpRequest.getPathWithoutQuery())) {
            return new HttpResponse(
                    new StatusLine(httpRequest.getVersionOfProtocol(), HttpStatus.OK), accept, DEFAULT_PAGE_CONTENT);
        }
        if (!httpRequest.checkQueryParamIsEmpty()) {
            return generateResponseForQueryParam(httpRequest);
        }

        Optional<ResponsePage> responsePage = ResponsePage.fromUrl(
                httpRequest.getPathWithoutQuery(), httpRequest.getCookie());
        if (responsePage.isPresent()) {
            ResponsePage page = responsePage.get();
            HttpResponse httpResponse = new HttpResponse(
                    new StatusLine(httpRequest.getVersionOfProtocol(), page.getStatus()),
                    accept,
                    FileReader.loadFileContent(page.getFileName()));
            httpResponse.addLocation(page.getFileName());
            return httpResponse;
        }
        return new HttpResponse(
                new StatusLine(httpRequest.getVersionOfProtocol(), HttpStatus.OK),
                accept,
                FileReader.loadFileContent(httpRequest.getPathWithoutQuery()));
    }

    private HttpResponse generateResponseForQueryParam(HttpRequest headers) {
        if (LOGIN_PATH.equals(headers.getPathWithoutQuery())) {
            return handleLoginRequest(headers);
        }
        throw new RuntimeException("'" + headers.getPathWithoutQuery() + "'는 정의되지 않은 URL입니다.");
    }

    private HttpResponse handleLoginRequest(HttpRequest httpRequest) {
        String accept = httpRequest.getFileType();
        Map<String, String> queryParams = httpRequest.getQueryParam();
        if (isMissingRequiredParams(httpRequest, queryParams)) {
            return new HttpResponse(
                    new StatusLine(httpRequest.getVersionOfProtocol(), HttpStatus.BAD_REQUEST),
                    accept,
                    FileReader.loadFileContent(BAD_REQUEST_PAGE));
        }

        Optional<User> user = authenticateUser(queryParams.get(ACCOUNT), queryParams.get(PASSWORD));
        if (user.isPresent()) {
            Session session = new Session(UUID.randomUUID().toString());
            session.setAttribute(session.getId(), user);
            SessionManager.getInstance().add(session);
            HttpCookie httpCookie = httpRequest.getCookie();
            String cookie = httpCookie.getCookies(session.getId());

            HttpResponse httpResponse
                    = new HttpResponse(
                    new StatusLine(httpRequest.getVersionOfProtocol(), HttpStatus.FOUND),
                    accept,
                    FileReader.loadFileContent(INDEX_PAGE));
            httpResponse.setCookie(cookie);
            httpResponse.addLocation(INDEX_PAGE);
            return httpResponse;
        }
        return new HttpResponse(
                new StatusLine(httpRequest.getVersionOfProtocol(), HttpStatus.UNAUTHORIZED),
                accept,
                FileReader.loadFileContent(UNAUTHORIZED_PAGE));
    }

    private boolean isMissingRequiredParams(HttpRequest httpRequest, Map<String, String> queryParams) {
        return httpRequest.getQueryParam().size() < 2 ||
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

    private HttpResponse generateResponseForPostUrl(HttpRequest httpRequest) {
        String url = httpRequest.getPathWithoutQuery();
        String accept = httpRequest.getFileType();
        if (REGISTER_PATH.equals(url)) {
            return handleRegistration(httpRequest.getVersionOfProtocol(), httpRequest.getBody(), accept);
        }
        return new HttpResponse(
                new StatusLine(httpRequest.getVersionOfProtocol(), HttpStatus.BAD_REQUEST),
                accept,
                FileReader.loadFileContent(NOT_FOUND_PAGE));
    }

    private HttpResponse handleRegistration(
            VersionOfProtocol versionOfProtocol,
            Map<String, String> bodyParams,
            String accept
    ) {
        String account = bodyParams.get(ACCOUNT);
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return new HttpResponse(
                    new StatusLine(versionOfProtocol, HttpStatus.BAD_REQUEST),
                    accept,
                    FileReader.loadFileContent(BAD_REQUEST_PAGE));
        }
        String password = bodyParams.get(PASSWORD);
        String email = bodyParams.get(EMAIL);
        InMemoryUserRepository.save(new User(account, password, email));
        HttpResponse httpResponse = new HttpResponse(
                new StatusLine(versionOfProtocol, HttpStatus.FOUND),
                accept,
                FileReader.loadFileContent(INDEX_PAGE));
        httpResponse.addLocation(INDEX_PAGE);
        return httpResponse;
    }
}
