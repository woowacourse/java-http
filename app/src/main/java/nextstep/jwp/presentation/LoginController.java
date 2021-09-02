package nextstep.jwp.presentation;

import java.awt.print.Pageable;
import java.io.IOException;
import nextstep.jwp.application.LoginService;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.web.StaticResourceReader;
import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.response.ContentType;
import nextstep.jwp.web.http.response.HttpResponse;
import nextstep.jwp.web.http.response.StatusCode;
import nextstep.jwp.web.session.HttpSession;

public class LoginController extends AbstractController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        if (request.getCookieValue("JSESSIONID") == null) {
            showLoginPage(request.getUrl(), response);
            return;
        }

        User user = (User) request.getSession()
            .getAttribute("user");

        if (user != null && InMemoryUserRepository.exist(user)) {
            redirectIndex(response);
        }
    }

    private void showLoginPage(String url, HttpResponse response) throws IOException {
        String responseBody =
            new StaticResourceReader(url + ContentType.HTML.getExtension()).content();

        response.setStatusLine(StatusCode.OK);
        response.addHeader("Content-Type", ContentType.HTML.getValue());
        response.addHeader("Content-Length", responseBody.getBytes().length + " ");
        response.addBody(responseBody);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        User loginUser =
            loginService.login(
                request.getAttribute("account"), request.getAttribute("password")
            );

        HttpSession session = request.getSession();
        session.setAttribute("user", loginUser);

        redirectIndex(response);
        response.addHeader("Set-Cookie", "JSESSIONID=" + session.getId());
    }

    private void redirectIndex(HttpResponse response) {
        response.setStatusLine(StatusCode.FOUND);
        response.addHeader("Location", "/index.html");
    }
}
