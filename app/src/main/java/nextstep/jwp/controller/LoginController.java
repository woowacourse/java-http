package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.*;
import nextstep.jwp.model.User;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class LoginController extends AbstractController {
    @Override
    void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        final HttpSession httpSession = httpRequest.getSession();
        if (httpSession != null && httpSession.hasAttribute("user")) {
            User user = (User) httpSession.getAttribute("user");
            HttpResponse response = new HttpResponse.Builder()
                    .outputStream(httpResponse.getOutputStream())
                    .status(HttpStatus.FOUND_302)
                    .redirectUrl("/index.html")
                    .build();
            response.forward();
            return;
        }
        HttpResponse response = new HttpResponse.Builder()
                .outputStream(httpResponse.getOutputStream())
                .status(HttpStatus.OK_200)
                .resource(httpRequest.getResource())
                .body("/login.html")
                .build();
        response.forward();

    }

    @Override
    void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        Map<String, String> params = httpRequest.getParams();
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(params.get("account"));

        if (optionalUser.isPresent() && optionalUser.get().checkPassword(params.get("password"))) {
            final HttpSession httpSession = generateSession(optionalUser);
            HttpResponse response = new HttpResponse.Builder()
                    .outputStream(httpResponse.getOutputStream())
                    .status(HttpStatus.FOUND_302)
                    .redirectUrl("/index.html")
                    .session(httpSession)
                    .build();
            response.forward();
            return;
        }
        HttpResponse response = new HttpResponse.Builder()
                .outputStream(httpResponse.getOutputStream())
                .status(HttpStatus.UNAUTHORIZED_401)
                .resource(httpRequest.getResource())
                .body("/401.html")
                .build();
        response.forward();
    }

    private HttpSession generateSession(Optional<User> optionalUser) {
        String uuid = UUID.randomUUID().toString();
        final HttpSession httpSession = new HttpSession(uuid);
        httpSession.setAttribute("user", optionalUser.get());
        HttpSessions.add(uuid, httpSession);
        return httpSession;
    }
}
