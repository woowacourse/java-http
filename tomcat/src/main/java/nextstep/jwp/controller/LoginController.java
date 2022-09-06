package nextstep.jwp.controller;

import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        if (request.hasHeader("Cookie")) {
            final Session session = request.getSession();
            log.info("Session Id: {}", session.getId());
            log.info("Login User: {}", session.getAttribute("user"));
            return HttpResponse.redirect("/index.html").build();
        }

        return HttpResponse.ok().fileBody("/login.html").build();
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        final String account = request.getRequestParam("account");
        final String password = request.getRequestParam("password");

        return createLoginResultResponse(account, password, request);
    }

    private HttpResponse createLoginResultResponse(final String account, final String password, final HttpRequest request) {
        final Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isEmpty()) {
            return HttpResponse.redirect("/401.html").build();
        }

        if (!user.get().checkPassword(password)) {
            return HttpResponse.redirect("/401.html").build();
        }

        final Session session = request.getSession();
        session.addAttribute("user", user.get());
        log.info("Login User: {}", user.get());
        return HttpResponse.redirect("/index.html").build();
    }

}
