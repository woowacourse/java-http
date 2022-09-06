package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NoSuchUserException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.RequestParameters;
import org.apache.coyote.http11.RequestUri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws Exception {
        RequestUri requestUri = httpRequest.getRequestUri();
        if (requestUri.hasRequestParameters()) {
            RequestParameters queryParameters = requestUri.getRequestParameters();
            login(httpResponse, queryParameters);
            return;
        }
        httpResponse.httpStatus(HttpStatus.OK)
                .body(FileReader.read(requestUri.parseFullPath()), requestUri.findMediaType());
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        RequestParameters requestParameters = httpRequest.getRequestParameters();
        login(httpResponse, requestParameters);
    }

    private void login(final HttpResponse httpResponse, final RequestParameters requestParameters) {
        try {
            User user = getUserByAccount(requestParameters.get("account"));
            validatePassword(requestParameters, user);
            log.info("user : " + user);
            httpResponse.httpStatus(HttpStatus.FOUND)
                    .addHeader("Location", "/index.html");
        } catch (NoSuchUserException e) {
            log.info(e.getMessage());
            httpResponse.httpStatus(HttpStatus.FOUND)
                    .addHeader("Location", "/401.html");
        }
    }

    private static void validatePassword(final RequestParameters queryParameters, final User user) {
        if (!user.checkPassword(queryParameters.get("password"))) {
            throw new NoSuchUserException("비밀번호가 일치하지 않습니다.");
        }
    }

    private User getUserByAccount(final String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new NoSuchUserException("존재하지 않는 회원입니다."));
    }
}
