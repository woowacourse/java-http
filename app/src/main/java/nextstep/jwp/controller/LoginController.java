package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.domain.HttpSession;
import nextstep.jwp.domain.HttpSessions;
import nextstep.jwp.domain.User;
import nextstep.jwp.domain.request.HttpRequest;
import nextstep.jwp.domain.request.RequestBody;
import nextstep.jwp.domain.response.HttpResponse;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class LoginController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        HttpResponse response = new HttpResponse();
        HttpSession session = request.getSession();

        return getRespond(request, response, session);
    }

    private HttpResponse getRespond(HttpRequest request, HttpResponse response, HttpSession session) throws IOException {
        if (Objects.nonNull(session) && Objects.nonNull(session.getAttribute("user"))) {
            response.setCookie(session, request.getHttpCookie());
            return response.redirect("/index.html");
        }
        return response.respond(request.getUri() + ".html");
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        RequestBody requestBody = request.getRequestBody();
        HttpResponse response = new HttpResponse();

        String account = requestBody.getParam("account");
        String password = requestBody.getParam("password");
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            return response.redirect("/400.html");
        }
        return postRespond(request, response, password, user.get());
    }

    private HttpResponse postRespond(HttpRequest request, HttpResponse response, String password, User user) {
        if (!user.checkPassword(password)) {
            return response.redirect("/401.html");
        }
        if (user.checkPassword(password)) {
            final HttpSession httpSession = request.getSession();
            httpSession.setAttribute("user", user);
            HttpSessions.put(httpSession.getId(), httpSession);
            request.assignSession(httpSession);
            response.setCookie(httpSession, request.getHttpCookie());
            return response.redirect("index.html");
        }
        return response.redirect("/index.html");
    }
}
