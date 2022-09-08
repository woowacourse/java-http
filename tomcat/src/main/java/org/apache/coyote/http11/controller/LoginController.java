package org.apache.coyote.http11.controller;


import static org.apache.coyote.http11.http.StatusCode.FOUND;
import static org.apache.coyote.http11.http.StatusCode.OK;
import static org.apache.coyote.http11.http.StatusCode.UNAUTHORIZED;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.Session;

public class LoginController extends AbstractController {

    private static final String REQUEST_URI = "/login";

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        Session session = httpRequest.getSession();
        if (session.getAttribute("login") != null) {
            httpResponse.send("/index.html", OK);
            return;
        }
        httpResponse.send("/login.html", OK);
    }


    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        RequestBody requestBody = request.getRequestBody();
        Map<String, String> parsedBody = requestBody.getParsedBody();

        if (InMemoryUserRepository.login(parsedBody.get("account"), parsedBody.get("password"))) {
            Session session = request.getSession();
            if (session != null) {
                User user = InMemoryUserRepository
                        .findByAccountAndPassword(parsedBody.get("account"), parsedBody.get("password"));
                session.setAttribute("login", user);
            }
            response.send("/index.html", FOUND);
            return;
        }

        response.send("/401.html", UNAUTHORIZED);
    }

    @Override
    public boolean canHandle(HttpRequest request) {
        return REQUEST_URI.equals(request.getResource());
    }
}
