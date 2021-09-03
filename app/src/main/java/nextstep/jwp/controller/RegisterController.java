package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.DBNotFoundException;
import nextstep.jwp.framework.http.common.HttpBody;
import nextstep.jwp.framework.http.common.HttpStatus;
import nextstep.jwp.framework.http.request.HttpRequest;
import nextstep.jwp.framework.http.response.HttpResponse;
import nextstep.jwp.model.User;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.create(request.getRequestLine(), request.getHeaders(), request.getBody(), HttpStatus.OK);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        final HttpStatus status = register(request.getBody());
        response.create(request.getRequestLine(), request.getHeaders(), request.getBody(), status);
    }

    private HttpStatus register(final HttpBody body) {
        try {
            return createAccount(body);
        } catch (DBNotFoundException ignored) {
            return HttpStatus.UNAUTHORIZED;
        }
    }

    private HttpStatus createAccount(final HttpBody body) {
        final Map<String, String> queryParams = body.getQueryParams();
        final String account = queryParams.get("account");
        final String password = queryParams.get("password");
        final String email = queryParams.get("email");

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            logger.debug("{}님은 이미 가입된 계정입니다.", account);
            return HttpStatus.UNAUTHORIZED;
        }

        InMemoryUserRepository.save(new User(2L, account, password, email));
        logger.debug("{}님의 새로운 계정이 생성 되었습니다.", account);
        return HttpStatus.CREATED;
    }
}
