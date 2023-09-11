package nextstep.jwp;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.AbstractController;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    private static final String BAD_REQUEST = "/400";
    private static final String CONFLICT = "/409";
    private static final String INDEX = "/index";
    private static final String REGISTER = "/register";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws IOException {
        final Map<String, String> requestBody = request.getRequestBody();

        final String account = requestBody.get(ACCOUNT);
        final String password = requestBody.get(PASSWORD);
        final String email = requestBody.get(EMAIL);

        if (account.isBlank() || password.isBlank() || email.isBlank()) {
            setResponse(response, BAD_REQUEST, HttpStatusCode.BAD_REQUEST);
            return;
        }

        if (InMemoryUserRepository.checkExistingId(account)) {
            setResponse(response, CONFLICT, HttpStatusCode.CONFLICT);
            return;
        }
        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        setResponse(response, INDEX, HttpStatusCode.OK);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        setResponse(response, REGISTER, HttpStatusCode.OK);
    }
}
