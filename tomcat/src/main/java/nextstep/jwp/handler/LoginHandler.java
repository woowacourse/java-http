package nextstep.jwp.handler;

import static org.apache.coyote.http11.ViewResolver.staticFileRequest;

import java.util.Map;
import java.util.Optional;

import org.apache.coyote.http11.HttpHeaderType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.UriParser;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.MemberNotFoundException;
import nextstep.jwp.model.User;

public class LoginHandler {

    private static final String LOGIN_PAGE = "/login.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    public static HttpResponse perform(HttpRequest request) {
        Map<String, String> queries = UriParser.parseUri(request.getUri());
        if (queries.isEmpty()) {
            return staticFileRequest(LOGIN_PAGE);
        }
        return performLogin(queries);
    }

    private static HttpResponse performLogin(Map<String, String> queries) {
        if (queries.size() != 2 || !queries.containsKey(ACCOUNT) || !queries.containsKey(PASSWORD)) {
            return new HttpResponse.Builder()
                    .statusCode(HttpStatus.NOT_FOUND)
                    .build();
        }

        Optional<User> findUser = InMemoryUserRepository.findByAccount(queries.get(ACCOUNT));
        User user = findUser.orElseThrow(MemberNotFoundException::new);
        if (!user.checkPassword(queries.get(PASSWORD))) {
            return new HttpResponse.Builder()
                    .statusCode(HttpStatus.FOUND)
                    .header(HttpHeaderType.LOCATION, UNAUTHORIZED_PAGE)
                    .build();
        }

        return new HttpResponse.Builder()
                .statusCode(HttpStatus.FOUND)
                .header(HttpHeaderType.LOCATION, "/index.html")
                .build();
    }
}
