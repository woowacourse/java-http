package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.catalina.Manager;
import org.apache.coyote.ForwardResult;
import org.apache.coyote.HttpStatusCode;
import org.apache.coyote.Session;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseHeader;

public class LoginController extends AbstractController {

    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";

    @Override
    public ForwardResult execute(HttpRequest request, HttpResponse response) {
        String body = request.getBody();
        Map<String, String> parsedBody = parseBody(body);

        String account = parsedBody.get(ACCOUNT_KEY);
        String password = parsedBody.get(PASSWORD_KEY);

        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        ResponseHeader header = new ResponseHeader();

        if (optionalUser.isPresent() && optionalUser.get().checkPassword(password)) {
            User user = optionalUser.get();
//            addSession(request, manager, user, header);
            return new ForwardResult(HttpStatusCode.OK, "index.html", header);
        }

        return new ForwardResult(HttpStatusCode.FOUND, "401.html", header);
    }

    private Map<String, String> parseBody(String query) {
        List<String> pairs = List.of(query.split("&"));
        return pairs.stream()
                .map(pair -> pair.split("=", 2))
                .collect(Collectors.toMap(s -> s[0], s -> s[1]));
    }

    private void addSession(HttpRequest request, Manager manager, User user, ResponseHeader header) {
        if (!existsSession(request)) {
            HttpSession session = Session.createRandomSession();
            manager.add(session);
            session.setAttribute("user", user.getAccount());
            header.setCookie(HttpCookie.ofJSessionId(session.getId()));
        }
    }

    private boolean existsSession(HttpRequest request) {
        RequestHeader requestHeaders = request.getHeaders();
        return requestHeaders.existsSession();
    }
}
