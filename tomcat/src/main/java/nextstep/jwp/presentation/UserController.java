package nextstep.jwp.presentation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.HttpCookie;
import org.apache.coyote.http11.http.HttpStatus;
import org.apache.coyote.http11.http.HttpHeaders;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.support.AbstractController;
import org.apache.coyote.http11.http.Session;
import org.apache.coyote.util.SessionManager;
import org.apache.coyote.util.CookieUtils;
import org.apache.coyote.util.FileUtils;

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
        String body = request.getBody();
        String[] split = body.split("&");
        Map<String, String> values = new HashMap<>();
        for (String value : split) {
            String[] keyAndValue = value.split("=");
            values.put(keyAndValue[0], keyAndValue[1]);
        }
        String account = values.get("account");
        Optional<User> byAccount = InMemoryUserRepository.findByAccount(account);
        if (byAccount.isPresent()) {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.redirect("/404.html");
            response.flush();
            return;
        }
        User user = new User(account, values.get("password"), values.get("email"));
        InMemoryUserRepository.save(user);

        String cookie = CookieUtils.ofJSessionId();
        Session session = new Session(cookie);
        session.setAttribute("user", user);
        SessionManager.add(session);
        response.setStatus(HttpStatus.FOUND);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.addLocation("/index.html ");
        httpHeaders.addCookie(HttpCookie.JSESSIONID + "=" + cookie);
        response.setHeaders(httpHeaders);
        response.flush();
    }
}
