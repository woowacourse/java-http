package nextstep.jwp.presentation;

import http.HttpHeaders;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpStatus;
import nextstep.jwp.application.AuthService;

public class RegisterController extends AbstractController {

    private static final String REDIRECT_PAGE_PATH = "/index.html";

    private final AuthService authService;

    private RegisterController(final AuthService authService) {
        this.authService = authService;
    }

    public static RegisterController instance() {
        return RegisterControllerHolder.instance;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) throws Exception {
        String responseBody = readResource(httpRequest);
        HttpHeaders responseHeaders = setResponseHeaders(httpRequest, responseBody);
        return new HttpResponse(HttpStatus.OK, responseHeaders, responseBody);
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        authService.signUp(httpRequest.getBody());
        return redirect(REDIRECT_PAGE_PATH);
    }

    protected HttpResponse redirect(final String redirectUrl) {
        HttpHeaders responseHeaders = HttpHeaders.createEmpty();
        responseHeaders.add("Location", redirectUrl);
        return new HttpResponse(HttpStatus.FOUND, responseHeaders, "");
    }

    public static class RegisterControllerHolder {

        private static final RegisterController instance = new RegisterController(AuthService.instance());
    }
}
