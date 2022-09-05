package nextstep.jwp.handler;

import static org.apache.coyote.http11.ViewResolver.staticFileRequest;

import java.util.Map;
import java.util.Optional;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.RequestParser;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.MemberNotFoundException;
import nextstep.jwp.model.User;

public class LoginHandler {

    private static final String INDEX_PAGE = "/index.html";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    public static HttpResponse perform(HttpRequest request) {
        Map<String, String> queries = RequestParser.parseUri(request.getUri());
        if (queries.isEmpty()) {
            return staticFileRequest(LOGIN_PAGE);
        }
        return performLogin(queries);
    }

    private static HttpResponse performLogin(Map<String, String> queries) {
        if (queries.size() != 2 || !queries.containsKey(ACCOUNT)
                || !queries.containsKey(PASSWORD)) {
            return HttpResponse.notFound();
        }

        Optional<User> findUser = InMemoryUserRepository.findByAccount(queries.get(ACCOUNT));
        User user = findUser.orElseThrow(MemberNotFoundException::new);
        if (!user.checkPassword(queries.get(PASSWORD))) {
            return HttpResponse.redirect(UNAUTHORIZED_PAGE);
        }

        return HttpResponse.redirect(INDEX_PAGE);
    }
}
