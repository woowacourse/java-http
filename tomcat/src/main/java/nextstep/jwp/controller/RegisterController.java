package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.DuplicationMemberException;
import nextstep.jwp.model.User;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.ResponseStatus;
import org.apache.front.AbstractController;

import java.util.Optional;

public class RegisterController extends AbstractController {

    public static final String ACCOUNT_KEY = "account";
    public static final String PASSWORD_KEY = "password";
    public static final String EMAIL_KEY = "email";

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        join(request, response);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        joinPage(request, response);
    }

    private void join(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final String account = httpRequest.getBodyValue(ACCOUNT_KEY);
        final String password = httpRequest.getBodyValue(PASSWORD_KEY);
        final String email = httpRequest.getBodyValue(EMAIL_KEY);

        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent()) {
            throw new DuplicationMemberException();
        }
        final User newUser = new User(account, password, email);
        InMemoryUserRepository.save(newUser);

        httpResponse.setViewPathAsBodyAndSetStatus(httpRequest.getPath(), ResponseStatus.MOVED_TEMP);
        httpResponse.setRedirect("/index.html");
    }

    private void joinPage(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        httpResponse.setViewPathAsBody(httpRequest.getPath());
    }
}
