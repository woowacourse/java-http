package nextstep.jwp.ui;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http.HttpHeader;
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
        session.getValue(SESSION_KEY)
               .ifPresentOrElse(user -> httpResponse.forward("/index.html"), () -> httpResponse.forward("/login.html"));
    }

    @Override
    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse, Session session) {
        Map<String, String> parameters = httpRequest.getParameters();
        String account = parameters.get("account");
        String password = parameters.get("password");

        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        if (optionalUser.isEmpty() || !optionalUser.get().checkPassword(password)) {
            httpResponse.addHeader(HttpHeader.HEADER_KEY.LOCATION.value, "/401.html");
        } else {
            User user = optionalUser.get();
            session.addValue("user", user);
            httpResponse.addHeader(HttpHeader.HEADER_KEY.SET_COOKIE.value, Session.REQUEST_COOKIE_KEY + "=" + session.getJsessionId());
            httpResponse.addHeader(HttpHeader.HEADER_KEY.LOCATION.value, "/index.html");
        }
        httpResponse.setStatusCode(StatusCode.FOUND);
    }
}
