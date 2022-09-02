package nextstep.jwp.handler;

import static nextstep.jwp.http.HttpVersion.HTTP_1_1;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NotFoundUserException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.model.User;

public class LoginRequestHandler implements HttpRequestHandler {

    @Override
    public HttpResponse handleHttpRequest(final HttpRequest httpRequest) {
        Map<String, String> queryParams = httpRequest.getQueryParams();
        String account = queryParams.get("account");
        String password = queryParams.get("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NotFoundUserException::new);
        if (user.checkPassword(password)) {
            return HttpResponse.found(HTTP_1_1);
        }
        return null;
    }
}
