package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.handler.AbstractController;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected String doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        return "register";
    }

    @Override
    protected String doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        RequestBody requestBody = httpRequest.getRequestBody();
        String account = requestBody.getValue("account");
        String email = requestBody.getValue("email");
        String password = requestBody.getValue("password");
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("user : {}", user);

        return "redirect:/index";
    }

    @Override
    public boolean isRest() {
        return false;
    }
}
