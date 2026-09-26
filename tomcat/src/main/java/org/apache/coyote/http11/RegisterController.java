package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterController extends AbstractController {

    private final Controller staticResourceController;

    public RegisterController(final Controller staticResourceController) {
        this.staticResourceController = staticResourceController;
    }

    @Override
    protected void doGet(
            final HttpRequest request,
            final HttpResponse response
    ) throws Exception {
        staticResourceController.service(request, response);
    }

    @Override
    protected void doPost(
            final HttpRequest request,
            final HttpResponse response
    ) {
        String account = request.findFormParameter("account")
                .orElseThrow(() -> new IllegalArgumentException("필수 입력값 누락: account"));
        String password = request.findFormParameter("password")
                .orElseThrow(() -> new IllegalArgumentException("필수 입력값 누락: password"));
        String email = request.findFormParameter("email")
                .orElseThrow(() -> new IllegalArgumentException("필수 입력값 누락: email"));

        InMemoryUserRepository.save(new User(account, password, email));

        response.sendRedirect("/index.html");
    }
}
