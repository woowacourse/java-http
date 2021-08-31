package nextstep.jwp.app.ui;

import java.io.IOException;
import nextstep.jwp.app.db.InMemoryUserRepository;
import nextstep.jwp.app.domain.User;
import nextstep.jwp.app.exception.UserNotFoundException;
import nextstep.jwp.http.RequestHandler;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                return response.sendRedirect("/401.html", HttpStatus.UNAUTHORIZED);
            }
            return response.sendRedirect("/index.html");
        } catch (UserNotFoundException e) {
            log.debug(e.getMessage());
            return response.sendRedirect("/401.html", HttpStatus.UNAUTHORIZED);
        }
    }
}
