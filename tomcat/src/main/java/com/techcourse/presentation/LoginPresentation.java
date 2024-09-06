package com.techcourse.presentation;

import com.techcourse.infrastructure.Presentation;
import com.techcourse.presentation.requestparam.UserRequestParam;
import com.techcourse.request.UserRequest;
import com.techcourse.service.LoginService;
import http.HttpMethod;
import http.requestheader.HttpStatusCode;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.coyote.ioprocessor.parser.HttpRequest;
import org.apache.coyote.ioprocessor.parser.HttpResponse;

public class LoginPresentation implements Presentation {

    private static final String URI_PATH = "/login";

    private final LoginService loginService;

    public LoginPresentation(LoginService loginService) {
        this.loginService = loginService;
    }

    public LoginPresentation() {
        this(new LoginService());
    }

    @Override
    public HttpResponse view(HttpRequest request) {
        UserRequestParam requestParam = new UserRequestParam(request.getQueryParam());
        UserRequest userRequest = requestParam.toObject();
        loginService.findUser(userRequest);
        String responseBody = loadLoginPage();
        return new HttpResponse(HttpStatusCode.OK, request.getMediaType(), responseBody);
    }

    private String loadLoginPage() {
        Path staticResourcePath = getStaticResourcePath();
        try {
            return Files.readString(staticResourcePath);
        } catch (IOException e) {
            throw new RuntimeException("파일을 읽을 수 없어요!");
        }
    }

    private Path getStaticResourcePath() {
        try {
            URL staticResourceUrl = getClass().getResource("/static/login.html");
            return Paths.get(staticResourceUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("파일 경로를 찾을 수 없네요..");
        }
    }

    @Override
    public boolean match(HttpRequest request) {
        return request.getHttpMethod() == HttpMethod.GET
                && URI_PATH.equals(request.getPath())
                && !request.getQueryParam().isEmpty();
    }
}
