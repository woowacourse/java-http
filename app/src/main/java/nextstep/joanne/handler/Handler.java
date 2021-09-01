package nextstep.joanne.handler;

import nextstep.joanne.db.InMemoryUserRepository;
import nextstep.joanne.exception.LoginFailedException;
import nextstep.joanne.exception.UserNotFoundException;
import nextstep.joanne.http.HttpMethod;
import nextstep.joanne.http.HttpStatus;
import nextstep.joanne.http.request.HttpRequest;
import nextstep.joanne.http.response.HttpResponse;
import nextstep.joanne.model.User;

public class Handler {
    private final HttpRequest httpRequest;

    public Handler(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    // uriContains 대신 uriStartsWith로 변경하기
    public HttpResponse handle() {
        if (httpRequest.uriContains("index")) {
            return ResourceTemplate.doPage(HttpStatus.OK, httpRequest.resourceUri());
        }
        if (httpRequest.uriContains("register")) {
            return doRegister();
        }
        if (httpRequest.uriContains("login")) {
            return doLogin();
        }
        return ResourceTemplate.doPage(HttpStatus.OK, httpRequest.resourceUri());
    }

    private HttpResponse doLogin() {
        if (httpRequest.isEqualsMethod(HttpMethod.GET)) {
            return ResourceTemplate.doPage(HttpStatus.OK, httpRequest.resourceUri());
        }

        if (httpRequest.isEqualsMethod(HttpMethod.POST)) {
            loginRequest(httpRequest.getFromRequestBody("account"),
                    httpRequest.getFromRequestBody("password"));
            return ResourceTemplate.doPage(HttpStatus.FOUND, "/index.html");
        }
        return null;
    }

    private HttpResponse doRegister() {
        if (httpRequest.isEqualsMethod(HttpMethod.GET)) {
            return ResourceTemplate.doPage(HttpStatus.OK, httpRequest.resourceUri());
        }
        if (httpRequest.isEqualsMethod(HttpMethod.POST)) {
            registerRequest();
            return ResourceTemplate.doPage(HttpStatus.FOUND, "/index.html");
        }
        return null;
    }

    private void loginRequest(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(UserNotFoundException::new);
        if (!user.checkPassword(password)) {
            throw new LoginFailedException();
        }
    }

    private void registerRequest() {
        final User user = new User(httpRequest.getFromRequestBody("account"),
                httpRequest.getFromRequestBody("password"),
                httpRequest.getFromRequestBody("email"));
        InMemoryUserRepository.save(user);
    }
}
