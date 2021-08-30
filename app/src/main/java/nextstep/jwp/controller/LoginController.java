package nextstep.jwp.controller;

import static nextstep.jwp.controller.IndexController.INDEX_RESOURCE_PATH;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.requestline.RequestURI;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.model.User;

public class LoginController extends AbstractController {

    private static final String LOGIN_PATH = "/login";
    private static final String LOGIN_RESOURCE_PATH = "/login.html";

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        return HttpResponse.of(HttpStatus.OK, new RequestURI(LOGIN_RESOURCE_PATH));
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        final RequestURI uri = request.getRequestLine().getRequestURI();
        String account = uri.getParamValue("account");
        String password = uri.getParamValue("password");

        final User user = InMemoryUserRepository.findByAccount(account)
            .orElseThrow(RuntimeException::new);

        if (!user.checkPassword(password)) {
            throw new RuntimeException();
        }

        return HttpResponse.redirect(INDEX_RESOURCE_PATH);
    }

    @Override
    protected String requestURI() {
        return LOGIN_PATH;
    }
}
