package nextstep.jwp.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.enums.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    private static final Controller INSTANCE = new RegisterController();

    public static Controller getInstance() {
        return INSTANCE;
    }

    private RegisterController() {
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        return HttpResponse.of(HttpStatusCode.OK, "/register.html");
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        final HttpRequestBody requestBody = httpRequest.getBody();
        final User user = createUser(requestBody);
        InMemoryUserRepository.save(user);

        return generateSuccessResponse();
    }

    private User createUser(final HttpRequestBody requestBody) {
        final String account = requestBody.findByKey("account");
        final String password = requestBody.findByKey("password");
        final String email = requestBody.findByKey("email");
        return new User(account, password, email);
    }

    private HttpResponse generateSuccessResponse() {
        final HttpResponse response = HttpResponse.of(HttpStatusCode.FOUND, "/register.html");
        response.addLocation("/login.html");
        return response;
    }
}
