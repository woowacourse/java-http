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

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Optional<String> jsessionid = request.getCookie(JSESSIONID);
        if (jsessionid.isPresent()) {
            loginService.loginWithSession(jsessionid.get(), response);
            return;
        }
        loginService.login(request, response);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        Optional<String> jsessionid = request.getCookie(JSESSIONID);
        if (jsessionid.isPresent()) {
            loginService.loginWithSession(jsessionid.get(), response);
            return;
        }
        response.setStatus(OK);
        response.setContentType(TEXT_HTML);
        response.setBody(ResourceLoader.load("static/login.html"));
    }
}
