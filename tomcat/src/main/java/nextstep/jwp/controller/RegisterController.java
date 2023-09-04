package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.PathResponse;
import org.apache.coyote.response.Response;

import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Optional;

public class RegisterController implements Controller {

    public static final String QUERY_ACCOUNT_KEY = "account";
    public static final String QUERY_PASSWORD_KEY = "password";
    public static final String QUERY_EMAIL_KEY = "email";

    @Override
    public Response handle(Request request) {
        if (request.isPost()) {
            return join(request);
        }
        return new PathResponse(request.getPath(), HttpURLConnection.HTTP_OK, "OK");
    }

    private Response join(Request request) {
        Map<String, String> requestBody = request.getRequestBody();

        if (!requestBody.containsKey(QUERY_ACCOUNT_KEY) || !requestBody.containsKey(QUERY_PASSWORD_KEY) || !requestBody.containsKey(QUERY_EMAIL_KEY)) {
            throw new IllegalArgumentException();
        }

        final String account = requestBody.get(QUERY_ACCOUNT_KEY);
        final String password = requestBody.get(QUERY_PASSWORD_KEY);
        final String email = requestBody.get(QUERY_EMAIL_KEY);

        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }

        final User newUser = new User(account, password, email);
        InMemoryUserRepository.save(newUser);

        return new PathResponse("/index", HttpURLConnection.HTTP_MOVED_TEMP, "Temporary Redirect");
    }
}
