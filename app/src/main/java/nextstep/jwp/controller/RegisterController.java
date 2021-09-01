package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.request.HttpMethod;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestBody;
import nextstep.jwp.model.User;

public class RegisterController extends AbstractController {

    private static final String URI_PATH = "/register";
    private static final String SUCCESS_REDIRECT_URL = "http://localhost:8080/index.html";

    @Override
    boolean isMatchingUriPath(HttpRequest httpRequest) {
        return URI_PATH.equals(httpRequest.getPath());
    }

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) {
        return super.renderPage(httpRequest);
    }

    @Override
    public HttpResponse doPost(HttpRequest httpRequest) {
        RequestBody requestBody = httpRequest.getRequestBody();
        String account = requestBody.getValue("account");
        String password = requestBody.getValue("password");
        String email = requestBody.getValue("email");
        InMemoryUserRepository.save(new User(account, password, email));

        return super.redirect(SUCCESS_REDIRECT_URL);
    }
}
