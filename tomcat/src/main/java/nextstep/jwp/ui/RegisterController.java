package nextstep.jwp.ui;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class RegisterController extends AbstractController {

    private static final RegisterController controller = new RegisterController();

    private RegisterController() {
    }

    public static RegisterController getController() {
        return controller;
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        return createGetResponseFrom(request);
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws Exception {
        InMemoryUserRepository.save(new User(request.getBodyParam("account"),
            request.getBodyParam("password"), request.getBodyParam("email")));

        return redirectTo("/index", HttpStatusCode.HTTP_STATUS_FOUND);
    }
}
