package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.Parameter;
import org.apache.coyote.http11.exception.ClientRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final String USER_SESSION_INFO_NAME = "user";
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.statusCode(HttpStatusCode.OK)
                .staticResource("/register.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Parameter param = request.getParameter();
        validateRegister(param);
        User newAccount = new User(param.getValue("account"), param.getValue("password"), param.getValue("email"));
        logger.info("user : {}", newAccount);
        InMemoryUserRepository.save(newAccount);
        response.statusCode(HttpStatusCode.FOUND)
                .createSession(USER_SESSION_INFO_NAME, newAccount)
                .redirect("index.html");
    }

    private void validateRegister(Parameter param) {
        validateRequiredParam(param);
        String account = param.getValue("account");
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent()) {
            throw new ClientRequestException(HttpStatusCode.BAD_REQUEST, "/400.html");
        }
    }

    private void validateRequiredParam(Parameter param) {
        if (param.getValue("account").isEmpty() || param.getValue("password").isEmpty()) {
            throw new ClientRequestException(HttpStatusCode.BAD_REQUEST, "/400.html");
        }
    }
}
