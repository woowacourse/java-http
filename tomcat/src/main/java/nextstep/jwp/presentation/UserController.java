package nextstep.jwp.presentation;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.http.HttpHeaders;
import org.apache.coyote.http11.http.HttpPath;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.HttpStatus;
import org.apache.coyote.http11.http.Session;
import org.apache.coyote.support.AbstractController;
import org.apache.coyote.util.CookieUtils;
import org.apache.coyote.util.FileUtils;
import org.apache.coyote.util.SessionManager;

public class UserController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        HttpPath httpPath = request.getPath();
        if (httpPath.isEqualToPath("/login")) {
            httpPath = new HttpPath("/login.html");
        }
        String body = FileUtils.readAllBytes(httpPath.getValue());
        response.forward(httpPath);
        response.setBody(body);
        response.write();
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        User user = getUser(request);

        Optional<User> byAccount = InMemoryUserRepository.findByAccount(user.getAccount());
        if (byAccount.isPresent()) {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.redirect("/404.html");
            response.write();
            return;
        }

        InMemoryUserRepository.save(user);
        redirect(response, user);
    }

    private User getUser(final HttpRequest httpRequest) {
        UserRequestHandler requestHandler = new UserRequestHandler();
        Map<String, String> attribute = requestHandler.handle(httpRequest);
        String account = attribute.get("account");
        String password = attribute.get("password");
        String email = attribute.get("email");
        return new User(account, password, email);
    }

    private void redirect(final HttpResponse response, final User user) throws IOException {
        String cookie = CookieUtils.ofJSessionId();

        Session session = new Session(cookie);
        session.setAttribute("user", user);
        SessionManager.add(session);
        response.setStatus(HttpStatus.FOUND);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.addLocation("/index.html ");
        httpHeaders.addCookie(Session.JSESSIONID + "=" + cookie);
        response.setHeaders(httpHeaders);
        response.write();
    }
}
