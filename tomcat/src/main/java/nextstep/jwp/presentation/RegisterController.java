package nextstep.jwp.presentation;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http.HttpParser;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    public String uri() {
        return "/register";
    }

    @Override
    public boolean support(final String uri, final String httpMethods) {
        return super.supportInternal(uri, httpMethods, this);
    }

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        doPost(request, response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final Map<String, String> userMap = HttpParser.parseQueryString(request.getBody());
        final String account = userMap.get("account");
        final String email = userMap.get("email");
        final String password = userMap.get("password");

        final User createUser = new User(account, password, email);

        InMemoryUserRepository.save(createUser);
        log.info("Create User: {}", createUser);

        response.sendRedirect("/index");
        log.info("Redirect: /index");
    }
}
