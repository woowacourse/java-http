package nextstep.jwp.commandcontroller;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import nextstep.jwp.common.HttpMethod;
import nextstep.jwp.common.HttpStatus;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.Session;
import nextstep.jwp.model.User;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.HttpResponse;
import nextstep.jwp.response.StatusLine;
import org.apache.catalina.SessionManager;

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
