package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.request.Http11RequestBody;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatusCode;
import java.util.Optional;

public class LoginController extends AbstractController {

    @Override
    protected void doPost(Http11Request request, Http11Response response) throws Exception {
        Http11RequestBody body = request.getRequestBody();
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(body.get("account"));
        response.setStatusCode(HttpStatusCode.FOUND);
        String redirectUri = "/401.html";

        User user;
        if (optionalUser.isPresent() && (user = optionalUser.get()).checkPassword(body.get("password"))) {
            Session session = Session.getInstance(user);
            SessionManager.add(session);
            response.addHeader("Set-Cookie", "JSESSIONID=" + session.getId());
            redirectUri = "/index.html";
        }

        response.addHeader("Location", " " + redirectUri);
    }

    @Override
    protected void doGet(Http11Request request, Http11Response response) throws Exception {
        super.doGet(request, response);
    }
}
