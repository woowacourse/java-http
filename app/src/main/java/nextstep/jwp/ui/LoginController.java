package nextstep.jwp.ui;

import nextstep.jwp.ui.request.HttpRequest;
import nextstep.jwp.ui.response.HttpResponse;
import nextstep.jwp.ui.request.RequestHandler;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        HttpResponse response = new HttpResponse();
        return response.forward(request.getPath() + ".html");
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws IOException {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        HttpResponse response = new HttpResponse();
        try {
            User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(UserNotFoundException::new);
            if (!user.checkPassword(password)) {
                return response.sendRedirect("/401.html");
            }
            return response.sendRedirect("/index.html");
        } catch (UserNotFoundException e) {
            log.debug(e.getMessage());
            return response.sendRedirect("/401.html");
        }
    }
}
