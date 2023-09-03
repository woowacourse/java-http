package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class HttpResponseMaker {

    public static final String CRLF = "\r\n";
    public static final String SPACE = " ";
    public static final String ACCOUNT = "account";
    public static final String PASSWORD = "password";
    public static final String STATIC_RESOURCE_DIR = "static";

    private static final Logger log = LoggerFactory.getLogger(HttpResponseMaker.class);

    public static String makeFrom(HttpRequestParser httpRequestParser) throws IOException {
        HttpRequestFirstLineInfo firstLineInfo = httpRequestParser.getHttpRequestFirstLineInfo();
        RequestMapper mapper = RequestMapper.findMapper(firstLineInfo);

        if (mapper == RequestMapper.LOG_IN_WITH_INFOS) {
            return handleLoginRequest(firstLineInfo, mapper);
        }

        String responseBody = "Hello world!";

        if (!mapper.getPath().equals("/")) {
            responseBody = readStaticResource(mapper);
        }

        return buildResponse(firstLineInfo, mapper, responseBody);
    }

    private static String handleLoginRequest(HttpRequestFirstLineInfo firstLineInfo, RequestMapper mapper) {
        Map<String, String> paramAndValues = firstLineInfo.getQueryStringParser().getParamAndValues();

        User user = InMemoryUserRepository.findByAccount(paramAndValues.get(ACCOUNT))
                .orElseThrow(() -> new IllegalArgumentException("회원 정보가 존재하지 않습니다."));

        if (!user.checkPassword(paramAndValues.get(PASSWORD))) {
            return buildRedirectResponse(firstLineInfo, RedirectLocation.LOG_IN_FAIL, mapper);
        }
        log.info("user: {}", user);

        return buildRedirectResponse(firstLineInfo, RedirectLocation.LOG_IN_SUCCESS, mapper);
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
        return firstLineInfo.getVersionOfTheProtocol() + SPACE + mapper.getHttpStatus().getStatusCode() + SPACE + mapper.getHttpStatus().getStatus() + SPACE;
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
