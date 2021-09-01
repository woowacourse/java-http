package nextstep.jwp.ui.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import nextstep.jwp.ui.RequestHandler;
import nextstep.jwp.ui.request.HttpRequest;
import nextstep.jwp.ui.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.forward(request.getPath() + ".html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        try {
            User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(UserNotFoundException::new);
            if (!user.checkPassword(password)) {
                throw new UnauthorizedException();
            }
            response.sendRedirect("/index.html");
        } catch (UserNotFoundException e) {
            log.error(e.getMessage());
            throw new UnauthorizedException();
        }
    }
}
