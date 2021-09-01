package nextstep.jwp.application.controller;

import java.util.Objects;
import java.util.Optional;
import nextstep.jwp.application.db.InMemoryUserRepository;
import nextstep.jwp.application.domain.User;
import nextstep.jwp.web.exception.ApplicationRuntimeException;
import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.request.body.HttpRequestBody;
import nextstep.jwp.web.http.response.HttpResponse;
import nextstep.jwp.web.http.response.HttpStatus;
import nextstep.jwp.web.http.session.HttpSession;
import nextstep.jwp.web.mvc.controller.AbstractController;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response)
        throws ApplicationRuntimeException {
        Object user = request.session().getAttribute("user");
        if (!Objects.isNull(user)) {
            response.setStatus(HttpStatus.FOUND);
            response.headers().add("Location", "/index");
        }
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        HttpRequestBody<?> body = request.body();

        String account = (String) body.getAttribute("account");
        Optional<User> foundAccount = InMemoryUserRepository.findByAccount(account);
        response.setStatus(HttpStatus.FOUND);

        if (foundAccount.isPresent()) {
            User user = foundAccount.get();
            boolean pass = user.checkPassword((String) body.getAttribute("password"));
            if (pass) {
                final HttpSession session = request.session();
                session.setAttribute("user", user);
                response.headers().setLocation("/index");
                return;
            }
        }
        response.headers().setLocation("/401.html");
    }
}
