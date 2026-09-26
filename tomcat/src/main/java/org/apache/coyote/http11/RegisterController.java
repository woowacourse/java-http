package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

final class RegisterController extends AbstractController {
    private final Controller staticResourceController;

    RegisterController(final Controller staticResourceController) {
        this.staticResourceController = staticResourceController;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws Exception {
        return staticResourceController.service(request);
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        final var parameters = RequestUri.parseParameters(request.body());
        final var user = new User(
                parameters.get("account"),
                parameters.get("password"),
                parameters.get("email")
        );

        InMemoryUserRepository.save(user);

        return ResponseFactory.redirect("/index.html", request);
    }
}
