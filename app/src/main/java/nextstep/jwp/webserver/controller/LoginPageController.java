package nextstep.jwp.webserver.controller;

import java.util.EnumSet;

import nextstep.jwp.framework.context.AbstractController;
import nextstep.jwp.framework.http.*;
import nextstep.jwp.framework.http.template.ResourceResponseTemplate;
import nextstep.jwp.framework.http.template.StringResponseTemplate;
import nextstep.jwp.webserver.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginPageController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginPageController.class);

    private final UserService userService;

    public LoginPageController() {
        super("/login", EnumSet.of(HttpMethod.GET, HttpMethod.POST));
        this.userService = new UserService();
    }

    @Override
    public HttpResponse doGet(HttpRequest httpRequest) {
        return new ResourceResponseTemplate().ok("/login.html");
    }

    @Override
    public HttpResponse doPost(HttpRequest httpRequest) {
        final Query query = new Query(httpRequest.getBody());
        final String account = query.get("account");
        final String password = query.get("password");

        LOGGER.debug("로그인 요청 - [account : {}, password : {}]", account, password);

        if (!userService.login(account, password)) {
            LOGGER.debug("로그인 실패!!");
            return new ResourceResponseTemplate().unauthorized("/401.html");
        }

        LOGGER.debug("로그인 성공!!");
        return new StringResponseTemplate().found("/index.html");
    }
}
