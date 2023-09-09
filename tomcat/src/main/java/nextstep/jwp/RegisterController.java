package nextstep.jwp;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.AbstractController;
import org.apache.coyote.http11.FileExtractor;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;

public class RegisterController extends AbstractController {

    private static final String BAD_REQUEST = "/400";
    private static final String CONFLICT = "/409";
    private static final String INDEX = "/index";
    private static final String REGISTER = "/register";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    @Override
    protected HttpResponse doPost(final HttpRequest request) throws IOException {
        final Map<String, String> requestBody = request.getRequestBody();

        final String account = requestBody.get(ACCOUNT);
        final String password = requestBody.get(PASSWORD);
        final String email = requestBody.get(EMAIL);

        if (account.isBlank() || password.isBlank() || email.isBlank()) {
            final ResponseBody responseBody = FileExtractor.extractFile(BAD_REQUEST);
            return HttpResponse.of(HttpStatusCode.BAD_REQUEST, responseBody);
        }

        if (InMemoryUserRepository.checkExistingId(account)) {
            final ResponseBody responseBody = FileExtractor.extractFile(CONFLICT);
            return HttpResponse.of(HttpStatusCode.OK, responseBody);
        }
        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        final ResponseBody responseBody = FileExtractor.extractFile(INDEX);
        return HttpResponse.of(HttpStatusCode.OK, responseBody);
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws IOException {
        final ResponseBody responseBody = FileExtractor.extractFile(REGISTER);
        return HttpResponse.of(HttpStatusCode.OK, responseBody);
    }
}
