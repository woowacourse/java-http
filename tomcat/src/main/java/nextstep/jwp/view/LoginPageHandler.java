package nextstep.jwp.view;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.BiFunction;
import org.apache.coyote.common.header.Cookie;
import org.apache.coyote.common.request.Request;
import org.apache.coyote.common.response.Response;
import org.apache.coyote.common.response.Status;
import org.apache.coyote.common.session.Session;
import org.apache.coyote.common.session.SessionManager;
import org.utils.ResourceGenerator;

public class LoginPageHandler implements BiFunction<Request, Response, Response> {

    @Override
    public Response apply(final Request request, final Response response) {
        final String sessionId = request.getCookie(Cookie.SESSION_ID_COOKIE_KEY)
                .orElseThrow(() -> new IllegalArgumentException("No such cookie"));
        Optional<Session> session = new SessionManager().findSession(sessionId);
        if (session.isPresent()) {
            final String indexPage = ResourceGenerator.getStaticResource("/index");
            return response.setStatus(Status.FOUND)
                    .setLocation("/index")
                    .setContentLength(indexPage.getBytes(StandardCharsets.UTF_8).length)
                    .setBody(indexPage);
        }
        final String loginPage = ResourceGenerator.getStaticResource("/login");
        return response.setContentLength(loginPage.getBytes(StandardCharsets.UTF_8).length)
                .setBody(loginPage);
    }
}
