package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.util.Map;
import com.techcourse.application.UserService;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        Map<String, String> queryString = httpRequest.getQueryString();

        if (queryString.containsKey("account") && queryString.containsKey("password")) {
            String account = queryString.get("account");
            String password = queryString.get("password");
            User user = new UserService().login(account, password);
            log.debug("user: {}", user);
        }

        httpResponse.setResponseBodyFile(httpRequest);
        httpResponse.write();
    }
}
