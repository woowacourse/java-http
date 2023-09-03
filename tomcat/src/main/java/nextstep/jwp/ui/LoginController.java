package nextstep.jwp.ui;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.StatusCode;
import org.apache.coyote.http.controller.HttpController;

import java.util.Map;
import java.util.Optional;

public class LoginController extends HttpController {

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.forward("/login.html");
    }

    @Override
    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        Map<String, String> parameters = httpRequest.getParameters();
        String account = parameters.get("account");
        String password = parameters.get("password");

        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        if (optionalUser.isEmpty() || !optionalUser.get().checkPassword(password)) {
            httpResponse.addHeader(HttpHeader.HEADER_KEY.LOCATION.value, "/401.html");
        } else {
            httpResponse.addHeader(HttpHeader.HEADER_KEY.LOCATION.value, "/index.html");
        }
        httpResponse.setStatusCode(StatusCode.FOUND);
    }
}
