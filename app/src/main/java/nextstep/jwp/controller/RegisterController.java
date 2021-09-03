package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.controller.AbstractController;
import nextstep.jwp.http.http_request.JwpHttpRequest;
import nextstep.jwp.http.http_response.JwpHttpResponse;
import nextstep.jwp.model.user.domain.User;

import java.io.IOException;

public class RegisterController extends AbstractController {

    private static final String RESOURCE_PREFIX = "static/";
    private static final String REGISTER_PAGE_PATH = "register.html";
    private static final String REGISTER_SUCCESS_PATH = "index.html";

    @Override
    public JwpHttpResponse doGet(JwpHttpRequest request) throws IOException {
        String resourceUri = RESOURCE_PREFIX + REGISTER_PAGE_PATH;
        String resourceFile = findResourceFile(resourceUri);
        return JwpHttpResponse.ok(resourceUri, resourceFile);
    }

    @Override
    public JwpHttpResponse doPost(JwpHttpRequest request) {
        User user = assembleUser(request);
        InMemoryUserRepository.save(user);
        return JwpHttpResponse.found(REGISTER_SUCCESS_PATH);
    }

    private User assembleUser(JwpHttpRequest jwpHttpRequest) {
        String account = jwpHttpRequest.getParam("account");
        String password = jwpHttpRequest.getParam("password");
        String email = jwpHttpRequest.getParam("email");
        return new User(account, password, email);
    }
}
