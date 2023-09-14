package nextstep.jwp.controller;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.coyote.common.HttpMethod;
import org.apache.coyote.common.HttpStatus;
import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.coyote.common.Session;
import nextstep.jwp.model.User;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.StatusLine;
import org.apache.coyote.common.SessionManager;

public class LoginController extends AbstractController {

    @Override
    public boolean canHandle(HttpRequest request) {
        final HttpMethod method = request.getHttpMethod();
        final String uri = request.getRequestUri();
        return method.equals(HttpMethod.POST) && uri.equals("login");
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        final Map<String, String> logInfo = Arrays.stream(request.getRequestBody().split("&"))
                .map(input -> input.split("="))
                .collect(Collectors.toMap(info -> info[0], info -> info[1]));

        response.setStatusLine(StatusLine.of(request.getHttpVersion(), HttpStatus.FOUND));
        final Optional<User> savedUser = InMemoryUserRepository.findByAccount(logInfo.get("account"));
        if (savedUser.isPresent()) {
            final User user = savedUser.get();
            if (user.checkPassword(logInfo.get("password"))) {
                response.addHeader("Location", "index.html");
                final Session session = new Session(UUID.randomUUID().toString());
                session.setAttribute("user", user);
                SessionManager.add(session);
                response.addCookie("JSESSIONID", session.getId());
                return;
            }
        }
        response.addHeader("Location", "401.html");
    }
}
