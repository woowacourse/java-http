package nextstep.jwp.controller;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.QueryString;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends Controller {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        QueryString queryString = request.queryString();
        if (queryString.isEmpty()) {
            response.setStatus(HttpStatus.FOUND);
            response.setHeader("Location", "/401.html");
            return;
        }
        if (isValidUser(queryString.get("account"), queryString.get("password"))) {
            response.setStatus(HttpStatus.FOUND);
            response.setHeader("Location", "/index.html");
            return;
        }
        response.setStatus(HttpStatus.FOUND);
        response.setHeader("Location", "/401.html");
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws Exception {
        super.doPost(request, response);
    }

    private static Boolean isValidUser(String account, String password) {
        Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);
        if (userOptional.isPresent() &&
                userOptional.get()
                        .checkPassword(password)) {
            User user = userOptional.get();
            log.info(user.toString());
            return true;
        }
        return false;
    }
}
