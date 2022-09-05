package nextstep.jwp.handler;

import static org.apache.coyote.http11.ViewResolver.staticFileRequest;

import java.util.Map;
import java.util.Optional;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.UriParser;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.InvalidLoginRequestException;
import nextstep.jwp.exception.MemberNotFoundException;
import nextstep.jwp.model.User;

public class LoginHandler {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    public static HttpResponse performLoginRequest(HttpRequest request) {
        Map<String, String> queries = UriParser.parseUri(request.getUri());
        if (queries.isEmpty()) {
            return staticFileRequest("/login.html");
        }
        return performLogin(queries);
    }

    private static HttpResponse performLogin(Map<String, String> queries) {
        try {
            LoginHandler loginHandler = new LoginHandler();
            User user = loginHandler.login(queries);

            return staticFileRequest("/index.html");
        } catch (InvalidLoginRequestException e) {
            return new HttpResponse.Builder()
                    .statusCode(HttpStatus.NOT_FOUND)
                    .build();
        }
    }

    public User login(Map<String, String> request) {
        if (request.size() != 2 || !request.containsKey(ACCOUNT) || !request.containsKey(PASSWORD)) {
            throw new InvalidLoginRequestException();
        }

        Optional<User> findUser = InMemoryUserRepository.findByAccount(request.get(ACCOUNT));
        User user = findUser.orElseThrow(MemberNotFoundException::new);
        user.checkPassword(request.get(PASSWORD));
        System.out.println(user);
        return user;
    }
}
