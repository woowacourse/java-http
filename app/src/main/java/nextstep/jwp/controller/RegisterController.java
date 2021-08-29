package nextstep.jwp.controller;

import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.service.UserService;

public class RegisterController extends Controller {
    private final UserService userService;

    public RegisterController(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        final String httpMethod = httpRequest.getHttpMethod();
        final String path = httpRequest.getPath();
        return "POST".equals(httpMethod) && "/register".equals(path);
    }

    @Override
    public HttpResponse doGet(HttpRequest httpRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpResponse doPost(final HttpRequest httpRequest) {
        userService.save(httpRequest.getPayload());

        final String redirectUrl = "/index.html";
        final String responseBody = readFile(redirectUrl);

        return new HttpResponse(
                httpRequest.getProtocol(),
                HttpStatus.SEE_OTHER,
                ContentType.findByUrl(redirectUrl),
                responseBody.getBytes().length,
                responseBody);
    }
}
