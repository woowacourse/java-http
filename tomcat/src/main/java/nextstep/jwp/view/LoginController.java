package nextstep.jwp.view;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.common.Charset;
import org.apache.coyote.common.MediaType;
import org.apache.coyote.common.controller.AbstractController;
import org.apache.coyote.common.header.Cookie;
import org.apache.coyote.common.request.Request;
import org.apache.coyote.common.response.Response;
import org.apache.coyote.common.response.Status;
import org.apache.coyote.common.session.Session;
import org.apache.coyote.common.session.SessionManager;
import org.utils.ResourceGenerator;

public class LoginController extends AbstractController {

    @Override
    protected void doPost(final Request request, final Response response) throws Exception {
        final String password = request.getBodyValue("password")
                .orElseThrow(() -> new IllegalArgumentException(Request.UNKNOWN_QUERY));
        final Optional<User> user = findUser(request);

        String locationUrl = "/index";
        String responseBody = "";
        Status statusCode = Status.FOUND;
        if (user.isEmpty() || !user.get().checkPassword(password)) {
            locationUrl = "/401";
            responseBody = ResourceGenerator.getStaticResource("/401");
            statusCode = Status.UNAUTHORIZED;
        }

        final String sessionId = request.getCookie(Cookie.SESSION_ID_COOKIE_KEY)
                .orElseThrow(() -> new IllegalArgumentException("No such session"));
        final Session session = new Session(sessionId);
        session.setAttribute("userId", user.get().getId());
        new SessionManager().add("userId", session);

        response.setContentType(MediaType.TEXT_HTML, Charset.UTF8)
                .setStatus(statusCode)
                .setContentLength(responseBody.getBytes(StandardCharsets.UTF_8).length)
                .setLocation(locationUrl)
                .setBody(responseBody)
                .build();
    }

    private Optional<User> findUser(final Request request) {
        final String userAccount = request.getBodyValue("account")
                .orElseThrow(() -> new IllegalArgumentException(Request.UNKNOWN_QUERY));
        return InMemoryUserRepository.findByAccount(userAccount);
    }

    @Override
    protected void doGet(final Request request, final Response response) throws Exception {
        final String sessionId = request.getCookie(Cookie.SESSION_ID_COOKIE_KEY)
                .orElseThrow(() -> new IllegalArgumentException("No such cookie"));
        Optional<Session> session = new SessionManager().findSession(sessionId);
        if (session.isPresent()) {
            final String indexPage = ResourceGenerator.getStaticResource("/index");
            response.setStatus(Status.FOUND)
                    .setLocation("/index")
                    .setContentLength(indexPage.getBytes(StandardCharsets.UTF_8).length)
                    .setBody(indexPage)
                    .build();
            return;
        }
        final String loginPage = ResourceGenerator.getStaticResource("/login");
        response.setContentLength(loginPage.getBytes(StandardCharsets.UTF_8).length)
                .setBody(loginPage)
                .build();
    }
}
