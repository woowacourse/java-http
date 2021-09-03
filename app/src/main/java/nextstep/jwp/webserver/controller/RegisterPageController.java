package nextstep.jwp.webserver.controller;

import java.util.EnumSet;

import nextstep.jwp.framework.context.AbstractController;
import nextstep.jwp.framework.http.HttpMethod;
import nextstep.jwp.framework.http.HttpRequest;
import nextstep.jwp.framework.http.HttpResponse;
import nextstep.jwp.framework.http.Query;
import nextstep.jwp.framework.http.template.RedirectResponseTemplate;
import nextstep.jwp.framework.http.template.ResourceResponseTemplate;
import nextstep.jwp.webserver.model.User;
import nextstep.jwp.webserver.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterPageController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterPageController.class);

    private final UserService userService;

    public RegisterPageController() {
        super("/register", EnumSet.of(HttpMethod.GET, HttpMethod.POST));
        this.userService = new UserService();
    }

    @Override
    public HttpResponse doGet(HttpRequest httpRequest) {
        return new ResourceResponseTemplate().ok("/register.html");
    }

    @Override
    public HttpResponse doPost(HttpRequest httpRequest) {
        final Query query = new Query(httpRequest.getBody());
        final String account = query.get("account");
        final String email = query.get("email");
        final String password = query.get("password");

        LOGGER.debug("회원가입 요청 - [account : {}, email : {}, password : {}]", account, email, password);

        final User user = new User(account, email, password);
        userService.register(user);

        return new RedirectResponseTemplate().found("/index.html");
    }
}
