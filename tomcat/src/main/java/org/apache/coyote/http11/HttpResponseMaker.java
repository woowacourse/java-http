package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.session.HttpCookie;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.apache.coyote.http11.RequestMapper.*;

public class HttpResponseMaker {

    public static final String COOKIE = "Cookie";
    private static final String CRLF = "\r\n";
    private static final String SPACE = " ";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String STATIC_RESOURCE_DIR = "static";
    private static final String EMAIL = "email";
    private static final Logger log = LoggerFactory.getLogger(HttpResponseMaker.class);

    public static String makeFrom(HttpRequestParser httpRequestParser, SessionManager sessionManager) throws IOException {
        HttpRequestFirstLineInfo firstLineInfo = httpRequestParser.getHttpRequestFirstLineInfo();
        RequestMapper mapper = RequestMapper.findMapper(firstLineInfo);
        Map<String, String> headers = httpRequestParser.getHeaders();
        Map<String, String> body = httpRequestParser.getBody();

        if (mapper == LOG_IN) {
            HttpCookie cookie = HttpCookie.from(headers.get(COOKIE));

            if (cookie.hasJSessionId() && sessionManager.findSession(cookie.getJSessionId()) != null) {
                return buildRedirectResponse(firstLineInfo, RedirectLocation.LOG_IN_SUCCESS, mapper);
            }

            return buildResponse(firstLineInfo, mapper, readStaticResource(mapper));
        }

        if (mapper == LOG_IN_WITH_INFOS) {
            return handleLoginRequest(firstLineInfo, headers, body, mapper, sessionManager);
        }

        if (mapper == REGISTER_WITH_INFOS) {
            return handleRegisterRequest(firstLineInfo, body, mapper);
        }

        String responseBody = "Hello world!";

        if (!mapper.getPath().equals("/")) {
            responseBody = readStaticResource(mapper);
        }

        return buildResponse(firstLineInfo, mapper, responseBody);
    }

    private static String handleLoginRequest(
            HttpRequestFirstLineInfo firstLineInfo,
            Map<String, String> headers,
            Map<String, String> body,
            RequestMapper mapper,
            SessionManager sessionManager
    ) {
        User user = InMemoryUserRepository.findByAccount(body.get(ACCOUNT))
                .orElseThrow(() -> new IllegalArgumentException("회원 정보가 존재하지 않습니다."));

        if (!user.checkPassword(body.get(PASSWORD))) {
            return buildRedirectResponse(firstLineInfo, RedirectLocation.LOG_IN_FAIL, mapper);
        }
        log.info("user: {}", user);

        String cookieHeader = headers.get(COOKIE);
        HttpCookie cookie = HttpCookie.from(cookieHeader);
        cookie.bake();
        Session session = new Session(cookie.getJSessionId());
        session.setAttribute("user", user);
        sessionManager.add(session);

        return String.join(CRLF,
                buildFirstLine(firstLineInfo, mapper),
                "Location:" + SPACE + RedirectLocation.LOG_IN_SUCCESS.getRedirectUrl(),
                "Set-Cookie:" + SPACE + "JSESSIONID=" + session.getId());
    }

    private static String handleRegisterRequest(
            HttpRequestFirstLineInfo firstLineInfo,
            Map<String, String> body,
            RequestMapper mapper
    ) {
        if (InMemoryUserRepository.findByAccount(body.get(ACCOUNT)).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 회원입니다.");
        }

        User user = new User(body.get(ACCOUNT), body.get(PASSWORD), body.get(EMAIL));
        InMemoryUserRepository.save(user);

        return buildRedirectResponse(firstLineInfo, RedirectLocation.REGISTER, mapper);
    }

    private static String readStaticResource(RequestMapper mapper) throws IOException {
        ClassLoader classLoader = HttpResponseMaker.class.getClassLoader();
        URL resource = classLoader.getResource(STATIC_RESOURCE_DIR + mapper.getPath());

        if (resource == null) {
            throw new FileNotFoundException("해당하는 파일을 찾을 수 없습니다.");
        }

        Path path = new File(resource.getFile()).toPath();

        return new String(Files.readAllBytes(path));
    }

    private static String buildRedirectResponse(
            HttpRequestFirstLineInfo firstLineInfo,
            RedirectLocation redirectLocation,
            RequestMapper mapper
    ) {
        return String.join(CRLF,
                buildFirstLine(firstLineInfo, mapper),
                "Location:" + SPACE + redirectLocation.getRedirectUrl());
    }

    private static String buildResponse(HttpRequestFirstLineInfo firstLineInfo, RequestMapper mapper, String responseBody) {
        return String.join(CRLF,
                buildFirstLine(firstLineInfo, mapper),
                "Content-Type:" + SPACE + mapper.getContentType().getType() + ";charset=utf-8" + SPACE,
                "Content-Length:" + SPACE + responseBody.getBytes().length + SPACE,
                "",
                responseBody);
    }

    private static String buildFirstLine(HttpRequestFirstLineInfo firstLineInfo, RequestMapper mapper) {
        return String.join(CRLF, firstLineInfo.getVersionOfTheProtocol() + SPACE + mapper.getHttpStatus().getStatusCode() + SPACE + mapper.getHttpStatus().getStatus() + SPACE);
    }

    private enum RedirectLocation {
        LOG_IN_SUCCESS("/index.html"),
        LOG_IN_FAIL("/401.html"),
        REGISTER("/index.html");

        private final String redirectUrl;

        RedirectLocation(String redirectUrl) {
            this.redirectUrl = redirectUrl;
        }

        public String getRedirectUrl() {
            return redirectUrl;
        }
    }
}
