package nextstep.jwp.handler;

import static org.apache.coyote.http11.ViewResolver.staticFileRequest;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.coyote.http11.model.HttpHeaderType;
import org.apache.coyote.http11.model.HttpStatus;
import org.apache.coyote.http11.model.RequestParser;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;

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
        if (request.getMethod().isGet() && queries.isEmpty()) {
            return staticFileRequest(LOGIN_PAGE);
        }
        if (request.getMethod().isPost() && queries.isEmpty()) {
            return performLogin(request);
        }
        return HttpResponse.notFound();
    }

    private static HttpResponse performLogin(HttpRequest request) {
        Optional<User> findUser = InMemoryUserRepository.findByAccount(request.getBodyValue(ACCOUNT));
        User user = findUser.orElseThrow(MemberNotFoundException::new);
        if (!user.checkPassword(request.getBodyValue(PASSWORD))) {
            return HttpResponse.redirect(UNAUTHORIZED_PAGE);
        }
        UUID cookie = UUID.randomUUID();
        return new HttpResponse.Builder()
                .statusCode(HttpStatus.FOUND)
                .addCookie(cookie)
                .header(HttpHeaderType.LOCATION, INDEX_PAGE)
                .build();
    }
}
