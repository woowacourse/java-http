package nextstep.jwp.handler;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.request.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    public static boolean handle(HttpRequest httpRequest, HttpResponse response) {
        Map<String, String> body = httpRequest.getParsedRequestBody();

        Optional<User> user = InMemoryUserRepository.findByAccount(body.get("account"));

        if (user.isPresent() && user.get().checkPassword(body.get("password"))) {
            log.info(user.toString());
            final var session = httpRequest.getSession();
            session.setAttribute("user", user.get());
            response.addCookie(HttpCookie.ofJSessionId(session.getId()));
            return true;
        }
        return false;
    }
}
