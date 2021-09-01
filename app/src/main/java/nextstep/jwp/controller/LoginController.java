package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.DBNotFoundException;
import nextstep.jwp.exception.PasswordMismatchException;
import nextstep.jwp.framework.http.HttpBody;
import nextstep.jwp.framework.http.HttpRequest;
import nextstep.jwp.framework.http.HttpResponse;
import nextstep.jwp.framework.http.HttpStatus;
import nextstep.jwp.model.User;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.create(request.getRequestLine(), request.getHeaders(), request.getBody(), HttpStatus.OK);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        final HttpStatus status = login(request.getBody());
        response.create(request.getRequestLine(), request.getHeaders(), request.getBody(), status);
    }

    private HttpStatus login(final HttpBody body) {
        try {
            checkAccount(body);
            return HttpStatus.FOUND;
        } catch (DBNotFoundException | PasswordMismatchException ignore) {
            return HttpStatus.UNAUTHORIZED;
        }
    }

    private void checkAccount(final HttpBody body) {
        final Map<String, String> queryParams = body.getQueryParams();
        final String account = queryParams.get("account");
        final String password = queryParams.get("password");
        final User user = InMemoryUserRepository.findByAccount(account).orElseThrow(DBNotFoundException::new);

        logger.debug(account + "님이 접속했습니다.");
        if (user.checkPassword(password)) {
            return;
        }
        throw new PasswordMismatchException();
    }
}
