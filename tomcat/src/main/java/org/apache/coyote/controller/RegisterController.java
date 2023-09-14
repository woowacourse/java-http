package org.apache.coyote.controller;

import static org.apache.coyote.http11.HttpStatus.FOUND;
import static org.apache.coyote.http11.HttpStatus.OK;

import java.util.Map;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    public static final int LOGIN_QUERY_PARAMS = 3;

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> queryParms = Utils.parseToQueryParms(request.getBody());

        validateQueryParms(queryParms);
        User user = new User(queryParms.get("account"), queryParms.get("password"),
                queryParms.get("email"));

        InMemoryUserRepository.save(user);
        response.setStatus(FOUND);
        response.setRedirectUrl("/index.html");
    }

    private void validateQueryParms(Map<String, String> queryParms) {
        long count = queryParms.entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .count();
        if (count != LOGIN_QUERY_PARAMS) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setStatus(OK);
        response.setContentType("text/html");
        response.setBody(Utils.readFile("static", "register.html"));
    }
}

