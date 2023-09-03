package nextstep.jwp.ui;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http.HttpHeader.HEADER_KEY;
import org.apache.coyote.http.controller.HttpController;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.StatusCode;

public class RegisterController extends HttpController {

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.forward("/register.html");
    }

    @Override
    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        Map<String, String> parameters = httpRequest.getParameters();
        String account = parameters.get("account");
        String email = parameters.get("email");
        String password = parameters.get("password");
        User user = new User(account, password, email);

        InMemoryUserRepository.save(user);
        httpResponse.setStatusCode(StatusCode.FOUND);
        httpResponse.addHeader(HEADER_KEY.LOCATION.value, "/index.html");
    }
}
