package nextstep.jwp.presentation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.http.HttpHeaders;
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
        String body = FileUtils.readAllBytes(request.getPath().getValue());
        response.setStatus(HttpStatus.OK);
        response.forward(request.getPath());
        response.setBody(body);
        response.flush();
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        Map<String, String> values = getUser(request);
        String account = values.get("account");
        String password = values.get("password");
        String email = values.get("email");

        Optional<User> byAccount = InMemoryUserRepository.findByAccount(account);
        if (byAccount.isPresent()) {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.redirect("/404.html");
            response.flush();
            return;
        }

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        redirect(response, user);
    }

    private Map<String, String> getUser(final HttpRequest httpRequest) {
        String body = httpRequest.getBody();
        String[] split = body.split("&");
        Map<String, String> values = new HashMap<>();
        for (String value : split) {
            String[] keyAndValue = value.split("=");
            values.put(keyAndValue[0], keyAndValue[1]);
        }
        return values;
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
        response.flush();
    }
}
