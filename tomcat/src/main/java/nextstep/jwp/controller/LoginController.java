package nextstep.jwp.controller;

import java.util.Map;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends AbstractController {
    
    private static final String INDEX_PAGE = "/index.html";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";

    private final SessionManager sessionManager = new SessionManager();

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final RequestBody requestBody = request.getRequestBody();
        final String account = requestBody.get("account");
        final String password = requestBody.get("password");

        final User user = InMemoryUserRepository.findByAccount(account).orElseThrow();
        if (!user.checkPassword(password)) {
            response.setHttpStatus(HttpStatus.UNAUTHORIZED).sendRedirect(UNAUTHORIZED_PAGE);
            return;
        }

        final String uuid = UUID.randomUUID().toString();
        response.setHttpStatus(HttpStatus.FOUND)
                .setCookie("JSESSIONID", uuid)
                .setSession(new Session(uuid, Map.of("user", user)))
                .addHeader("Location", INDEX_PAGE)
                .sendRedirect(INDEX_PAGE);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        if (request.getSession() == null) {
            response.setHttpStatus(HttpStatus.OK).sendRedirect(LOGIN_PAGE);
            return;
        }
        response.setHttpStatus(HttpStatus.FOUND)
                .addHeader("Location", INDEX_PAGE)
                .sendRedirect(INDEX_PAGE);
    }
}
