package nextstep.jwp.controller;

import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NoSuchUserException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.RequestParameters;
import org.apache.coyote.http11.RequestUri;
import org.apache.coyote.http11.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws Exception {
        RequestUri requestUri = httpRequest.getRequestUri();
        if (requestUri.hasRequestParameters()) {
            login(httpRequest, httpResponse);
            return;
        }
        httpResponse.httpStatus(HttpStatus.OK)
                .body(FileReader.read(requestUri.parseFullPath()), requestUri.findMediaType());
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        login(httpRequest, httpResponse);
    }

    private void login(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        try {
            RequestParameters requestParameters = httpRequest.getRequestParameters();
            User user = getUserByAccount(requestParameters.get("account"));
            validatePassword(requestParameters, user);
            Session session = httpRequest.getSession();
            session.addAttribute("user", user);
            httpResponse.httpStatus(HttpStatus.FOUND)
                    .redirect("/index.html")
                    .setCookie(HttpCookie.ofJSessionId(session.getId()));
            log.info("user : " + user);
        } catch (NoSuchUserException e) {
            log.info(e.getMessage());
            httpResponse.httpStatus(HttpStatus.FOUND)
                    .redirect("/401.html");
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
