package org.apache.coyote.http11.servlet;

import java.io.IOException;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.Session;
import org.apache.coyote.http11.common.request.HttpRequest;
import org.apache.coyote.http11.common.request.QueryParams;
import org.apache.coyote.http11.common.response.HttpResponse;
import org.apache.coyote.http11.common.response.StatusCode;
import org.apache.coyote.http11.exception.BadRequestException;
import org.apache.coyote.http11.util.Parser;
import org.apache.coyote.http11.util.StaticFileLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginServlet extends Servlet {

    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);
    public static final String ACCOUNT = "account";
    public static final String PASSWORD = "password";
    public static final String USER = "user";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        System.out.println("LoginServlet.doGet");
        if (isLoggedIn(request)) {
            response.setStatusCode(StatusCode.FOUND);
            response.setLocation(Page.INDEX);
        } else {
            String content = StaticFileLoader.load(Page.LOGIN.getUri());

            response.setStatusCode(StatusCode.OK);
            response.setContentType(ContentType.TEXT_HTML);
            response.setContentLength(content.getBytes().length);
            response.setBody(content);
        }
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        QueryParams params = Parser.parseToQueryParams(request.getBody().getContent());
        String account = params.getParam(ACCOUNT);
        String password = params.getParam(PASSWORD);

        if (account.isEmpty() || password.isEmpty()) {
            throw new BadRequestException();
        }
        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresentOrElse(
                        user -> loginSuccess(request, response, user),
                        () -> loginFail(response)
                );
    }

    private boolean isLoggedIn(HttpRequest request) {
        Optional<Session> session = request.getSession(false);
        if (session.isEmpty()) {
            return false;
        }
        Optional<Object> attribute = session.get().getAttribute(USER);
        if (attribute.isEmpty()) {
            return false;
        }

        User userInSession = (User) attribute.get();
        if (isValidUser(userInSession)) {
            return true;
        }
        session.get().removeAttribute(USER);
        return false;
    }

    private boolean isValidUser(final User userInSession) {
        return InMemoryUserRepository.findByAccount(userInSession.getAccount())
                .filter(userInDb -> userInDb.checkPassword(userInSession.getPassword()))
                .isPresent();
    }

    private void loginSuccess(HttpRequest request, HttpResponse response, User user) {
        log.info("로그인 성공 : {}", user);
        Session session = request.getSession(true).get();
        session.setAttribute(USER, user);

        response.setStatusCode(StatusCode.FOUND);
        response.setLocation(Page.INDEX);
        response.setCookie("JSESSIONID", session.getId());
    }

    private void loginFail(HttpResponse response) {
        response.setStatusCode(StatusCode.FOUND);
        response.setLocation(Page.UNAUTHORIZED);
    }
}
