package nextstep.jwp.controller;

import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.authentication.HttpSession;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;

import java.util.Map;

public class LoginController extends AbstractController {
    private final UserService userService;

    public LoginController(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        final HttpMethod httpMethod = httpRequest.getHttpMethod();
        final String path = httpRequest.getPath();
        return (httpMethod.isGet() || httpMethod.isPost()) && "/login".equals(path);
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        if (httpRequest.doesNotHaveJSession()) {
            return super.doGet(httpRequest);
        }
        final String redirectUrl = "/index.html";
        return new HttpResponse(
                httpRequest.getProtocol(),
                HttpStatus.FOUND,
                redirectUrl);
    }

    @Override
    public HttpResponse doPost(final HttpRequest httpRequest) {
        try {
            final Map<String, String> payload = httpRequest.getPayload();
            final String account = payload.get("account");
            final String password = payload.get("password");

            final User user = userService.login(account, password);
            final HttpSession httpSession = httpRequest.getSession();
            httpSession.setAttribute("user", user);

            final String redirectUrl = "/index.html";
            return new HttpResponse(
                    httpRequest.getProtocol(),
                    HttpStatus.FOUND,
                    httpSession.getId(),
                    redirectUrl);
        } catch (Exception exception) {
            final String unauthorizedUrl = "/401.html";
            final String responseBody = readFile(unauthorizedUrl);

            return new HttpResponse(
                    httpRequest.getProtocol(),
                    HttpStatus.findHttpStatusByUrl(unauthorizedUrl),
                    ContentType.findByUrl(unauthorizedUrl),
                    responseBody.getBytes().length,
                    responseBody);
        }
    }
}
