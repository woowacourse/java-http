package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestBody;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.User;

import static nextstep.jwp.http.request.HttpMethod.GET;

public class RegisterController extends AbstractController {

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
        final String email = requestBody.getParameter("email");
        final String password = requestBody.getParameter("password");
        final User signupUser = new User(account, password, email);
        LOG.debug("회원가입 요청 account : {}", signupUser.getAccount());
        LOG.debug("회원가입 요청 email : {}", signupUser.getEmail());
        LOG.debug("회원가입 요청 password : {}", signupUser.getPassword());
        InMemoryUserRepository.save(signupUser);
        response.assignStatusCode(302);
        response.assignLocationHeader("http://localhost:8080/index.html");
    }
}
