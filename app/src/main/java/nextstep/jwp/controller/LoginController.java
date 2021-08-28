package nextstep.jwp.controller;

import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.service.UserService;

import java.util.Map;

public class LoginController extends Controller {
    private final UserService userService;

    public LoginController(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        final String httpMethod = httpRequest.getHttpMethod();
        final String path = httpRequest.getPath();

        return "GET".equals(httpMethod) && path.startsWith("/login");
    }

    // TODO: login.html form 설정 post로 바꾸기
    @Override
    public HttpResponse doPost(final HttpRequest httpRequest) {
        try {
            final Map<String, String> queryParameters = httpRequest.getQueryParameters();
            final String account = queryParameters.get("account");
            final String password = queryParameters.get("password");
            userService.login(account, password);

            final String redirectUrl = "/index.html";
            final String responseBody = readFile(redirectUrl);

            return new HttpResponse(
                    httpRequest.getProtocol(),
                    HttpStatus.FOUND,
                    "text/html",
                    responseBody.getBytes().length,
                    responseBody);
        } catch (Exception exception) {
            final String unauthorizedUrl = "/401.html";
            final String responseBody = readFile(unauthorizedUrl);

            return new HttpResponse(
                    httpRequest.getProtocol(),
                    HttpStatus.UNAUTHORIZED,
                    "text/html",
                    responseBody.getBytes().length,
                    responseBody);
        }
    }
}
