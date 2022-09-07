package nextstep.jwp.view;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.BiFunction;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.common.Charset;
import org.apache.coyote.common.MediaType;
import org.apache.coyote.common.header.Cookie;
import org.apache.coyote.common.request.Request;
import org.apache.coyote.common.response.Response;
import org.apache.coyote.common.response.Status;
import org.apache.coyote.common.session.Session;
import org.apache.coyote.common.session.SessionManager;
import org.utils.ResourceGenerator;

public class LoginHandler implements BiFunction<Request, Response, Response> {

    @Override
    public Response apply(final Request request, final Response response) {
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

        return response.setContentType(MediaType.TEXT_HTML, Charset.UTF8)
                .setStatus(statusCode)
                .setContentLength(responseBody.getBytes(StandardCharsets.UTF_8).length)
                .setLocation(locationUrl)
                .setBody(responseBody);
    }

    private Optional<User> findUser(final Request request) {
        final String userAccount = request.getBodyValue("account")
                .orElseThrow(() -> new IllegalArgumentException(Request.UNKNOWN_QUERY));
        return InMemoryUserRepository.findByAccount(userAccount);
    }
}
