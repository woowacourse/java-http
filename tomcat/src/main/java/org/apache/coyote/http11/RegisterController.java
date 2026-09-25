package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private final StaticResourceController staticResourceController;

    public RegisterController(final StaticResourceController staticResourceController) {
        this.staticResourceController = staticResourceController;
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        Map<String, String> params = request.params();
        User user = new User(params.get("account"), params.get("password"), params.get("email"));
        InMemoryUserRepository.save(user);

        log.info("register user: {}", user);
        return HttpResponse.redirect("/index.html", null);
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws Exception {
        return staticResourceController.serveResource(request);
    }
}
