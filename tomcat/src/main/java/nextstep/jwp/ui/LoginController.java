package nextstep.jwp.ui;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http.HeaderKey;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.StatusCode;
import org.apache.coyote.http.controller.HttpController;

import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http.session.Session;

public class LoginController extends HttpController {

    private static final String SESSION_KEY = "user";

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse, Session session) {
        Optional<Object> optionalUser = session.getValue(SESSION_KEY);
        if (optionalUser.isEmpty()) {
            httpResponse.forward("/login.html");
            return;
        }
        httpResponse.setStatusCode(StatusCode.FOUND);
        httpResponse.addHeader(HeaderKey.LOCATION.value, "/index.html");
    }

    @Override
    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse, Session session) {
        Map<String, String> parameters = httpRequest.getParameters();
        String account = parameters.get("account");
        String password = parameters.get("password");

        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        if (optionalUser.isEmpty() || !optionalUser.get().checkPassword(password)) {
            httpResponse.addHeader(HeaderKey.LOCATION.value, "/401.html");
        } else {
            User user = optionalUser.get();
            session.addValue("user", user);
            httpResponse.addHeader(
                HeaderKey.SET_COOKIE.value, Session.COOKIE_KEY + "=" + session.getId());
            httpResponse.addHeader(HeaderKey.LOCATION.value, "/index.html");
        }
        httpResponse.setStatusCode(StatusCode.FOUND);
    }
}
