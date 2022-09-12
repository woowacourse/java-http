package nextstep.jwp.controller;

import static org.apache.coyote.http11.handler.StaticResourceHandler.readFile;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.InvalidPasswordException;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.handler.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        try {
            final HttpResponse response = HttpResponse.found(request.getHttpVersion(), request.getContentType(),
                    "/index.html");
            login(request, response);
            return response;
        } catch (RuntimeException e) {
            return HttpResponse.found(request.getHttpVersion(), request.getContentType(), "/401.html");
        }
    }

    private void login(final HttpRequest request, final HttpResponse response) {
        final User user = InMemoryUserRepository.findByAccount(request.getRequestBodyValue("account"))
                .orElseThrow(UserNotFoundException::new);
        final boolean isCorrectPassword = user.checkPassword(request.getRequestBodyValue("password"));
        if (!isCorrectPassword) {
            throw new InvalidPasswordException();
        }
        log.info(String.format("user : %s", user));
        response.addCookie(SessionManager.create(user));
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws IOException {
        return HttpResponse.ok(request.getHttpVersion(), request.getContentType(), readFile(request.getHttpPath()));
    }
}
