package nextstep.jwp.controller;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends Controller {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatus.FOUND);
        response.setHeader("Location", "/login.html");
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response){
        Map<String, String> bodyForm = request.body()
                .toApplicationForm();
        if (bodyForm.containsKey("account") && bodyForm.containsKey("password")) {
            login(bodyForm.get("account"), bodyForm.get("password"), response);
            return;
        }
        response.setStatus(HttpStatus.FOUND);
        response.setHeader("Location", "/401.html");
    }

    private void login(String account, String password, HttpResponse response) {
        Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);
        if (userOptional.isPresent() &&
                userOptional.get()
                        .checkPassword(password)) {
            User user = userOptional.get();
            log.info(user.toString());
            response.setStatus(HttpStatus.FOUND);
            response.setHeader("Location", "/index.html");
            UUID uuid = UUID.randomUUID();
            response.setHeader("Set-Cookie", "JSESSIONID=" + uuid);
        }
    }
}
