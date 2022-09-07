package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http.AbstractController;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.RequestBody;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;

public class RegisterController extends AbstractController {

    private static final RegisterController INSTANCE = new RegisterController();

    private RegisterController() {
    }

    public static RegisterController getInstance() {
        return INSTANCE;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        return HttpResponse.init(HttpStatusCode.OK)
                .setBodyByPath("/register.html");
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        final RequestBody requestBody = httpRequest.getRequestBody();

        final User user = new User(requestBody.get("account"), requestBody.get("password"), requestBody.get("email"));
        InMemoryUserRepository.save(user);

        final Session session = Session.generate();
        session.setAttribute("user", user);
        SessionManager.add(session);

        return HttpResponse.init(HttpStatusCode.FOUND)
                .setLocationAsHome()
                .addCookie("JSESSIONID", session.getId());
    }
}
