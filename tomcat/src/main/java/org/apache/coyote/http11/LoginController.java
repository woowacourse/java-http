package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;

public class LoginController extends AbstractController {

    private final Manager sessionManager;
    private final Controller staticResourceController;

    public LoginController(
            final Manager sessionManager,
            final Controller staticResourceController
    ) {
        this.sessionManager = sessionManager;
        this.staticResourceController = staticResourceController;
    }

    @Override
    protected void doGet(
            final HttpRequest request,
            final HttpResponse response
    ) throws Exception {
        if (request.session().getAttribute("user") instanceof User) {
            response.sendRedirect("/index.html");
            return;
        }

        staticResourceController.service(request, response);
    }

    @Override
    protected void doPost(
            final HttpRequest request,
            final HttpResponse response
    ) {
        String account = request.findFormParameter("account")
                .orElseThrow(() -> new IllegalArgumentException("필수 입력값 누락: account"));
        String password = request.findFormParameter("password")
                .orElseThrow(() -> new IllegalArgumentException("필수 입력값 누락: password"));

        Optional<User> authenticatedUser = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));

        if (authenticatedUser.isEmpty()) {
            response.sendRedirect("/401.html");
            return;
        }

        Session renewedSession = sessionManager.renewSession(request.session());
        renewedSession.setAttribute("user", authenticatedUser.get());

        response.sendRedirect("/index.html");
        response.addHeader(
                "Set-Cookie",
                "JSESSIONID=" + renewedSession.getId()
        );
    }
}
