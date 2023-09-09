package org.apache.coyote.controller;

import static org.apache.coyote.http11.HttpStatus.FOUND;
import static org.apache.coyote.http11.HttpStatus.OK;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> queryParms = Utils.parseToQueryParms(request.getBody());
        User user = new User(queryParms.get("account"), queryParms.get("password"),
                queryParms.get("email"));
        InMemoryUserRepository.save(user);
        response.setStatus(FOUND);
        response.setRedirectUrl("/index.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setStatus(OK);
        response.setContentType("text/html");
        response.setRedirectUrl("/index.html");
        response.setBody(Utils.readFile("static", "register.html"));
    }
}

