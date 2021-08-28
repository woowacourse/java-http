package nextstep.jwp.controller;

import nextstep.jwp.exception.UnAuthorizedException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestBody;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.service.LoginService;
import nextstep.jwp.staticresource.StaticResourceFinder;

import static nextstep.jwp.http.request.HttpMethod.GET;

public class LoginController extends AbstractController {

    private final LoginService loginService;

    public LoginController(StaticResourceFinder staticResourceFinder, LoginService loginService) {
        super(staticResourceFinder);
        this.loginService = loginService;
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request.hasMethod(GET)) {
            doGet(request, response);
            return;
        }
        doPost(request, response);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        assignStaticResourceByUriToResponse(request, response, ".html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        final RequestBody requestBody = request.getBody();
        final String account = requestBody.getParameter("account");
        final String password = requestBody.getParameter("password");
        LOG.debug("로그인 요청 account : {}", account);
        LOG.debug("로그인 요청 password : {}", password);
        try {
            loginService.login(account, password);
            LOG.debug("로그인 성공!!");
            assignRedirectToResponse(response, "http://localhost:8080/index.html");
        } catch (UnAuthorizedException e) {
            LOG.debug("로그인 실패");
            assignRedirectToResponse(response, "http://localhost:8080/401.html");
        }
    }
}
