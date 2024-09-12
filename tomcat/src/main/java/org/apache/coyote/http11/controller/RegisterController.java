package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.QueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends Controller {

    private static final String USER_SESSION_INFO_NAME = "user";
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        QueryParam param = request.getQueryParam();
        User newAccount = new User(param.getValue("account"), param.getValue("password"), param.getValue("email"));
        logger.info("user : {}", newAccount);
        InMemoryUserRepository.save(newAccount);
        return HttpResponse.builder()
                .statusCode(HttpStatusCode.FOUND)
                .createSession(USER_SESSION_INFO_NAME, newAccount)
                .redirect("index.html");
    }
}
