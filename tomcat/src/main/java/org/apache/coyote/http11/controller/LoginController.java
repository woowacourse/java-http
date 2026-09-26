package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (isLoggedIn(request)) {
            response.sendRedirect(request.getVersion(), "", "/index.html");
            return;
        }

        super.writeStaticResource(request, response, request.getPath() + ".html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        if (!request.hasBodyParameters("account", "password")) {
            response.sendRedirect(request.getVersion(), "", "/401.html");
            return;
        }
        Optional<User> userOpt = InMemoryUserRepository.findByAccount(request.getBodyParameter("account"));
        if (userOpt.isEmpty()) {
            response.sendRedirect(request.getVersion(), "", "/401.html");
            return;
        }

        User user = userOpt.get();
        String password = request.getBodyParameter("password");
        if (user.checkPassword(password)) {
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            log.info("로그인 성공! 아이디: {}", user.getAccount());
            String jsessionid = decideJsessionidToSet(request, session.getId());
            response.sendRedirect(request.getVersion(), jsessionid, "/index.html");
            return;
        }
        response.sendRedirect(request.getVersion(), "", "/401.html");
    }

    private boolean isLoggedIn(HttpRequest httpRequest) throws IOException {
        HttpSession session = httpRequest.getSession(false);
        return session != null && session.getAttribute("user") != null;
    }

    private String decideJsessionidToSet(HttpRequest httpRequest, String otherJsessionid) {
        String jsessionid = httpRequest.getJsessionid();
        if (jsessionid.equals(otherJsessionid)) {
            return "";
        }
        return otherJsessionid;
    }
}
