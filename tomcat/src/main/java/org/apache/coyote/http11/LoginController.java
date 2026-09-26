package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final SessionSupport.SessionContext context = SessionSupport.from(request);
        final HttpSession session = SessionSupport.find(context);
        if (session != null && session.getAttribute("user") != null) {
            redirect(response, context, "/index.html");
            return;
        }
        response.setHeader("Content-Type", "text/html;charset=utf-8");
        response.setBody(StaticResourceController.load("/login"));
        SessionSupport.addCookie(response, context);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final SessionSupport.SessionContext context = SessionSupport.from(request);
        final Map<String, String> parameters = parseForm(request.getBody());
        final User user = InMemoryUserRepository.findByAccount(parameters.get("account"))
                .filter(foundUser -> foundUser.checkPassword(parameters.get("password")))
                .orElse(null);
        if (user == null) {
            redirect(response, context, "/401.html");
            return;
        }

        HttpSession session = SessionSupport.find(context);
        if (session == null) {
            session = SessionSupport.create(context);
        }
        session.setAttribute("user", user);
        redirect(response, context, "/index.html");
    }

    private void redirect(final HttpResponse response, final SessionSupport.SessionContext context,
                          final String location) {
        response.setStatus(302);
        response.setHeader("Location", location);
        SessionSupport.addCookie(response, context);
    }

    private Map<String, String> parseForm(final String body) {
        final Map<String, String> parameters = new HashMap<>();
        for (final String parameter : body.split("&")) {
            final String[] pair = parameter.split("=", 2);
            if (pair.length == 2) {
                parameters.put(pair[0], URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
            }
        }
        return parameters;
    }
}
