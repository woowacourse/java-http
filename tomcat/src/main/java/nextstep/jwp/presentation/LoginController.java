package nextstep.jwp.presentation;

import static org.apache.coyote.http11.ContentType.TEXT_HTML;
import static org.apache.coyote.http11.HttpStatus.OK;

import java.util.Optional;
import nextstep.jwp.common.ResourceLoader;
import nextstep.jwp.service.LoginService;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class LoginController extends AbstractController {

    private static final String JSESSIONID = "JSESSIONID";

    private final ResourceLoader resourceLoader;
    private final LoginService loginService;

    public LoginController(ResourceLoader resourceLoader, LoginService loginService) {
        this.resourceLoader = resourceLoader;
        this.loginService = loginService;
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        Optional<String> jsessionid = request.getCookie(JSESSIONID);
        if (jsessionid.isPresent()) {
            return loginService.loginWithSession(jsessionid.get());
        }
        return loginService.login(request);
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        Optional<String> jsessionid = request.getCookie(JSESSIONID);
        if (jsessionid.isPresent()) {
            return loginService.loginWithSession(jsessionid.get());
        }
        return HttpResponse.status(OK)
                .body(resourceLoader.load("static/login.html"))
                .contentType(TEXT_HTML)
                .build();
    }
}
