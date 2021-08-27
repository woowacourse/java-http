package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnAuthorizedException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestBody;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.User;

import static nextstep.jwp.http.request.HttpMethod.GET;

public class LoginController extends AbstractController {

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
        final User loginUser = new User(account, password);
        LOG.debug("로그인 요청 account : {}", loginUser.getAccount());
        LOG.debug("로그인 요청 password : {}", loginUser.getPassword());
        login(response, loginUser);
    }

    private void login(HttpResponse response, User loginUser) {
        String filePath = null;
        try {
            authorizeLoginUser(loginUser);
            LOG.debug("로그인 성공!!");
            filePath = "/index.html";
        } catch (UnAuthorizedException e) {
            LOG.debug("로그인 실패");
            filePath = "/401.html";
        } finally {
            response.assignStatusCode(302);
            response.assignLocationHeader("http://localhost:8080" + filePath);
        }
    }

    private void authorizeLoginUser(User loginUser) {
        final User foundUser = InMemoryUserRepository.findByAccount(loginUser.getAccount())
                .orElseThrow(() -> new UnAuthorizedException("존재하지 않는 account 입니다."));
        foundUser.validatePassword(loginUser.getPassword());
    }
}
