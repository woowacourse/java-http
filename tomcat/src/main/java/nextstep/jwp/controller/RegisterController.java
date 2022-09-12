package nextstep.jwp.controller;

import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class RegisterController extends AbstractController {

    private static final String INDEX_PAGE = "/index.html";
    private static final String REGISTER_PAGE = "/register.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    private static final RegisterController INSTANCE = new RegisterController();

    public static RegisterController getInstance() {
        return INSTANCE;
    }

    private RegisterController() {
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        ResourceController.returnResource(REGISTER_PAGE, response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        User user = new User(request.getBodyValue(ACCOUNT), request.getBodyValue(PASSWORD),
                request.getBodyValue(EMAIL));
        InMemoryUserRepository.save(user);
        HttpResponse.redirect(response, INDEX_PAGE);
    }
}
