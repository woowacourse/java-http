package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.common.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends AbstractController {

    private static final String INDEX_PAGE = "/index.html";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";

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

        final Session session = request.getSession();
        session.setAttribute("user", user);
        response.setHttpStatus(HttpStatus.FOUND)
                .setCookie("JSESSIONID", session.getId())
                .setSession(session)
                .addHeader("Location", INDEX_PAGE)
                .sendRedirect(INDEX_PAGE);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final Session session = request.getSession();
        if (session.getAttribute("user") == null) {
            response.setHttpStatus(HttpStatus.OK).sendRedirect(LOGIN_PAGE);
            return;
        }
        response.setHttpStatus(HttpStatus.FOUND)
                .addHeader("Location", INDEX_PAGE)
                .sendRedirect(INDEX_PAGE);
    }
}
