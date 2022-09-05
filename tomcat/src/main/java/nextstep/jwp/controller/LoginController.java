package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NoSuchUserException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.QueryParameters;
import org.apache.coyote.http11.RequestUri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws Exception {
        RequestUri requestUri = httpRequest.getRequestUri();
        if (requestUri.hasQueryParams()) {
            login(requestUri.getQueryParams());
        }
        httpResponse.httpStatus(HttpStatus.OK)
                .body(FileReader.read(requestUri.parseFullPath()), requestUri.findMediaType());
    }

    private void login(final QueryParameters queryParameters) {
        User user = getUserByAccount(queryParameters.get("account"));
        if (user.checkPassword(queryParameters.get("password"))) {
            log.info("user : " + user);
            return;
        }
        log.info("비밀번호가 일치하지 않습니다.");
    }

    private User getUserByAccount(final String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new NoSuchUserException("존재하지 않는 회원입니다."));
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {

    }
}
