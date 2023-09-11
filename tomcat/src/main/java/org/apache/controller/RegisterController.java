package org.apache.controller;

import nextstep.jwp.common.HttpHeader;
import nextstep.jwp.common.HttpRequest;
import nextstep.jwp.common.HttpResponse;
import nextstep.jwp.common.Reader;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static nextstep.jwp.common.StatusCode.FOUND;
import static nextstep.jwp.common.StatusCode.OK;
import static org.reflections.Reflections.log;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        Map<String, String> userInfo = Reader.readUserInfo(httpRequest.getRequestBody());
        User user = new User(userInfo.get("account"), userInfo.get("password"), userInfo.get("email"));
        InMemoryUserRepository.save(user);
        log.info("등록 성공 : user = {}", user);

        httpResponse.setVersion("HTTP/1.1");
        httpResponse.setStatusCode(FOUND);
        httpResponse.setHttpHeader(HttpHeader.from(
                List.of("Location: /index.html")
        ));
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws URISyntaxException {
        Path path = readPath(httpRequest.getHttpLine().getUrl());
        byte[] bytes = Reader.readBytes(path);
        String responseBody = new String(bytes);
        HttpHeader httpResponseHeader = HttpHeader.from(List.of(
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: " + responseBody.getBytes().length));

        httpResponse.setVersion("HTTP/1.1");
        httpResponse.setStatusCode(OK);
        httpResponse.setHttpHeader(httpResponseHeader);
        httpResponse.setResponseBody(responseBody);
    }

    private Path readPath(String url) throws URISyntaxException {
        URL resourcePath = getClass().getClassLoader().getResource("static" + url + ".html");
        return Path.of(resourcePath.toURI());
    }

}
