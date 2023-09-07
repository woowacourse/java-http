package org.apache.coyote.http11.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpHeaderName;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.Session;
import org.apache.coyote.http11.common.request.HttpMethod;
import org.apache.coyote.http11.common.request.HttpRequest;
import org.apache.coyote.http11.common.request.QueryParams;
import org.apache.coyote.http11.common.response.HttpResponse;
import org.apache.coyote.http11.common.response.StatusCode;
import org.apache.coyote.http11.util.Parser;
import org.apache.coyote.http11.util.StaticFileLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginServlet implements Servlet {

    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);
    public static final String ACCOUNT = "account";
    public static final String PASSWORD = "password";
    public static final String USER = "user";

    @Override
    public HttpResponse handle(final HttpRequest request) throws IOException {
        if (request.getMethod() == HttpMethod.GET) {
            return doGet(request);
        }
        if (request.getMethod() == HttpMethod.POST) {
            return doPost(request);
        }
        return HttpResponse.createMethodNotAllowed(List.of(HttpMethod.GET, HttpMethod.POST));
    }

    private HttpResponse doGet(final HttpRequest request) throws IOException {
        if (isLoggedIn(request)) {
            HttpHeaders headers = new HttpHeaders();
            headers.addHeader(HttpHeaderName.LOCATION, Page.INDEX.getUri());
            return HttpResponse.create(StatusCode.FOUND, headers);
        }
        String content = StaticFileLoader.load(Page.LOGIN.getUri());
        HttpHeaders headers = new HttpHeaders();
        headers.addHeader(HttpHeaderName.CONTENT_TYPE, ContentType.TEXT_HTML.getDetail());
        headers.addHeader(HttpHeaderName.CONTENT_LENGTH, String.valueOf(content.getBytes().length));
        return HttpResponse.create(StatusCode.OK, headers, content);
    }

    private HttpResponse doPost(final HttpRequest request) throws IOException {
        QueryParams params = Parser.parseToQueryParams(request.getBody().getContent());

        if (params.getParam(ACCOUNT).isEmpty() || params.getParam(PASSWORD).isEmpty()) {
            return HttpResponse.createBadRequest();
        }

        String account = params.getParam(ACCOUNT).get();
        String password = params.getParam(PASSWORD).get();

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> loginSuccess(request, user))
                .orElseGet(this::loginFail);
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

    private HttpResponse loginSuccess(HttpRequest request, User user) {
        log.info("로그인 성공 : {}", user);
        Session session = request.getSession(true).get();
        session.setAttribute(USER, user);

        HttpHeaders headers = new HttpHeaders();
        headers.addHeader(HttpHeaderName.LOCATION, Page.INDEX.getUri());
        headers.addHeader(HttpHeaderName.SET_COOKIE, "JSESSIONID=" + session.getId());
        return HttpResponse.create(StatusCode.FOUND, headers);
    }

    private HttpResponse loginFail() {
        HttpHeaders headers = new HttpHeaders();
        headers.addHeader(HttpHeaderName.LOCATION, Page.UNAUTHORIZED.getUri());
        return HttpResponse.create(StatusCode.FOUND, headers);
    }
}
