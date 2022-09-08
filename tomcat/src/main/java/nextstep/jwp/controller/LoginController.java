package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NoSuchUserException;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestParameters;
import org.apache.coyote.http11.request.RequestUri;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws Exception {
        Session session = httpRequest.getSession(false);
        User user = (User) session.getAttribute("user");
        if (user != null) {
            httpResponse.httpStatus(HttpStatus.FOUND)
                    .redirect("/index.html");
            return;
        }
        RequestUri requestUri = httpRequest.getRequestUri();
        httpResponse.httpStatus(HttpStatus.OK)
                .body(FileReader.read(requestUri.parseFullPath()), requestUri.findMediaType());
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        RequestParameters requestParameters = httpRequest.getRequestParameters();
        User user = InMemoryUserRepository.getUserByAccount(requestParameters.get("account"));
        validatePassword(user, requestParameters.get("password"));
        login(httpRequest, httpResponse, user);
    }

    private void validatePassword(final User user, final String password) {
        if (!user.checkPassword(password)) {
            throw new NoSuchUserException("비밀번호가 일치하지 않습니다.");
        }
    }

    private void login(final HttpRequest httpRequest, final HttpResponse httpResponse, final User user) {
        Session session = httpRequest.getSession(true);
        session.addAttribute("user", user);

        httpResponse.redirect("/index.html")
                .setCookie(HttpCookie.ofJSessionId(session.getId()));

        log.info("user : " + user);
    }
}
