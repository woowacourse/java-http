package nextstep.jwp.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.apache.coyote.http11.utils.PathFinder;
import org.apache.coyote.http11.utils.QueryParamsParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws Exception {
        final Path path = PathFinder.findPath("/login.html");
        final String responseBody = new String(Files.readAllBytes(path));
        final Optional<User> loginUser = request.findUserByJSessionId();
        if (loginUser.isPresent()) {
            return redirectByAlreadyLogin(responseBody);
        }
        return new HttpResponse(HttpStatus.OK, responseBody, ContentType.HTML);
    }

    private HttpResponse redirectByAlreadyLogin(final String responseBody) {
        return new HttpResponse(HttpStatus.FOUND, responseBody, ContentType.HTML, "/index.html");
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) throws Exception {
        final HashMap<String, String> loginData = QueryParamsParser.parseByBody(request.getRequestBody());
        final Path path = PathFinder.findPath("/login.html");
        final String responseBody = new String(Files.readAllBytes(path));
        return makeLoginResponse(loginData, responseBody);
    }

    private HttpResponse makeLoginResponse(final Map<String, String> loginData, final String responseBody) {
        final String account = loginData.get("account");
        final String password = loginData.get("password");
        if (loginData.isEmpty() || !isSuccessLogin(account, password)) {
            log.info("로그인에 실패했습니다.");
            return failLoginResponse(responseBody);
        }
        final User user = new User(account, password);
        return successLoginResponse(responseBody, user);
    }

    private HttpResponse successLoginResponse(final String responseBody, final User user) {
        final HttpResponse httpResponse =
                new HttpResponse(HttpStatus.FOUND, responseBody, ContentType.HTML, "/index.html");
        final Session session = new Session();
        session.setAttribute("user", user);
        SessionManager.add(session);
        httpResponse.addJSessionId(session);
        return httpResponse;
    }

    private HttpResponse failLoginResponse(final String responseBody) {
        log.info("로그인 계정 정보가 이상합니다. responseBody={}", responseBody);
        return new HttpResponse(HttpStatus.FOUND, responseBody, ContentType.HTML, "/401.html");
    }

    private boolean isSuccessLogin(final String account, final String password) {
        final Optional<User> accessUser = InMemoryUserRepository.findByAccount(account);
        return accessUser.filter(user -> checkPassword(password, user))
                .isPresent();
    }

    private boolean checkPassword(final String password, final User accessUser) {
        if (!accessUser.checkPassword(password)) {
            return false;
        }
        log.info("user = {}", accessUser);
        return true;
    }
}
