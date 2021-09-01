package nextstep.jwp.controller;

import static nextstep.jwp.controller.IndexController.INDEX_RESOURCE_PATH;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.common.Body;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.requestline.RequestURI;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.http.util.ParamExtractor;
import nextstep.jwp.model.User;

public class LoginController extends AbstractController {

    private static final String LOGIN_RESOURCE_PATH = "/login.html";
    private static final String UNAUTHORIZED_PATH = "/401.html";

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        return HttpResponse.of(HttpStatus.OK, new RequestURI(LOGIN_RESOURCE_PATH));
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        try {
            final Body body = request.getBody();
            final Map<String, String> params = ParamExtractor.extractParams(body.asString());
            String account = params.get("account");
            String password = params.get("password");

            final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(UnauthorizedException::new);

            if (!user.checkPassword(password)) {
                throw new UnauthorizedException();
            }
            return HttpResponse.redirect(INDEX_RESOURCE_PATH);
        } catch (UnauthorizedException e) {
            return HttpResponse.redirect(UNAUTHORIZED_PATH);
        }
    }
}
