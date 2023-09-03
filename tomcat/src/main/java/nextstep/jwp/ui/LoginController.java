package nextstep.jwp.ui;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpResponseComposer;
import org.apache.coyote.http.StatusCode;
import org.apache.coyote.http.controller.HttpController;

import java.util.Map;
import java.util.Optional;

public class LoginController extends HttpController {

    @Override
    public void doPost(HttpRequest httpRequest, HttpResponseComposer httpResponseComposer) {
        Map<String, String> parameters = httpRequest.getParameters();
        HttpResponse.Builder builder = new HttpResponse.Builder()
                .statusCode(StatusCode.FOUND);

        String account = parameters.get("account");
        String password = parameters.get("password");

        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        if (optionalUser.isEmpty() || !optionalUser.get().checkPassword(password)) {
            builder.location("/401.html");
        } else {
            builder.location("/index.html");
        }
        httpResponseComposer.setHttpResponse(builder.build());
    }

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponseComposer httpResponseComposer) {
        httpResponseComposer.setViewName("/login.html");
    }
}
