package org.apache.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.common.HttpHeader;
import org.apache.common.HttpRequest;
import org.apache.common.HttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.common.StatusCode.FOUND;
import static org.apache.common.StatusCode.OK;
import static org.reflections.Reflections.log;

public class RegisterController extends AbstractController {

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws URISyntaxException, IOException {
        super.service(httpRequest, httpResponse);
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        Map<String, String> userInfo = readUserInfo(httpRequest.getRequestBody());
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
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws URISyntaxException, IOException {
        Path path = readPath(httpRequest.getHttpLine().getUrl());
        byte[] bytes = readBytes(path);
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

    private byte[] readBytes(Path path) throws IOException {
        return Files.readAllBytes(path);
    }

    private Map<String, String> readUserInfo(String body) {
        return Arrays.stream(body.split("&"))
                .map(value -> value.split("="))
                .collect(Collectors.toMap(value -> value[0], value -> value[1]));
    }
}
