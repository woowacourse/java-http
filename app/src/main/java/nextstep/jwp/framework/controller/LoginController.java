package nextstep.jwp.framework.controller;

import java.util.Map;
import nextstep.jwp.framework.infrastructure.http.content.ContentType;
import nextstep.jwp.framework.infrastructure.http.method.HttpMethod;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.http.response.HttpResponse;
import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;
import nextstep.jwp.web.application.UserService;

public class LoginController extends AbstractController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean canProcess(HttpRequest httpRequest) {
        HttpMethod httpMethod = httpRequest.getMethod();
        String url = httpRequest.getUrl();
        return url.equals("/login") &&
            (httpMethod.equals(HttpMethod.GET) || httpMethod.equals(HttpMethod.POST));
    }

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) {
        String url = "/login.html";
        return new HttpResponse.Builder()
            .protocol(httpRequest.getProtocol())
            .httpStatus(HttpStatus.OK)
            .contentType(ContentType.find(url))
            .responseBody(readFile(url))
            .build();
    }

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) {
        Map<String, String> attributes = httpRequest.getContentAsAttributes();
        try {
            userService.login(attributes.get("account"), attributes.get("password"));
            String redirectUrl = "/index.html";
            return new HttpResponse.Builder()
                .protocol(httpRequest.getProtocol())
                .httpStatus(HttpStatus.FOUND)
                .contentType(ContentType.find(redirectUrl))
                .location(redirectUrl)
                .responseBody(readFile(redirectUrl))
                .build();
        } catch (RuntimeException runtimeException) {
            String errorUrl = "/401.html";
            return new HttpResponse.Builder()
                .protocol(httpRequest.getProtocol())
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .contentType(ContentType.find(errorUrl))
                .responseBody(readFile(errorUrl))
                .build();
        }
    }
}
