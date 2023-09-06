package org.apache.coyote.http11.servlet;

import java.io.IOException;
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
    public static final String INDEX_PAGE = "/index.html";
    public static final String LOGIN_PAGE = "/login.html";
    public static final String LOGIN_FAIL_PAGE = "/401.html";

    @Override
    public HttpResponse handle(final HttpRequest request) throws IOException {
        if (request.getMethod() == HttpMethod.GET) {
            return doGet(request);
        }
        if (request.getMethod() == HttpMethod.POST) {
            return doPost(request);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.addHeader(HttpHeaderName.ALLOW, HttpMethod.GET+", "+HttpMethod.POST);
        return HttpResponse.create(StatusCode.METHOD_NOT_ALLOWED, headers);
    }

    private HttpResponse doGet(final HttpRequest request) throws IOException {
        if (isLoggedIn(request)) {
            HttpHeaders headers = new HttpHeaders();
            headers.addHeader(HttpHeaderName.LOCATION, INDEX_PAGE);
            return HttpResponse.create(StatusCode.FOUND, headers);
        }
        String content = StaticFileLoader.load(LOGIN_PAGE);
        HttpHeaders headers = new HttpHeaders();
        headers.addHeader(HttpHeaderName.CONTENT_TYPE, ContentType.TEXT_HTML.getDetail());
        headers.addHeader(HttpHeaderName.CONTENT_LENGTH, String.valueOf(content.getBytes().length));
        return HttpResponse.create(StatusCode.OK, headers, content);
    }

    private HttpResponse doPost(final HttpRequest request) {
        QueryParams params = Parser.parseToQueryParams(request.getBody().getContent());
        String account = params.getParam(ACCOUNT);
        String password = params.getParam(PASSWORD);

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> loginSuccess(request, user))
                .orElseGet(() -> loginFail());
    }

    private boolean isLoggedIn(HttpRequest request) {
        Object value = request.getSession().getAttribute(USER); // TODO : 세션 새로만들었을 경우..?
        if (value == null) {
            return false;
        }
        User userInSession = (User) value;
        return InMemoryUserRepository.findByAccount(userInSession.getAccount())
                .filter(user -> user.checkPassword(userInSession.getPassword()))
                .isPresent();
    }

    private HttpResponse loginSuccess(HttpRequest request, User user) {
        log.info("로그인 성공 : {}", user);
        Session session = request.getSession();
        session.setAttribute(USER, user);

        HttpHeaders headers = new HttpHeaders();
        headers.addHeader(HttpHeaderName.LOCATION, INDEX_PAGE);
        headers.addHeader(HttpHeaderName.SET_COOKIE, "JSESSIONID=" + session.getId());
        return HttpResponse.create(StatusCode.FOUND, headers);
    }

    private HttpResponse loginFail() {
        HttpHeaders headers = new HttpHeaders();
        headers.addHeader(HttpHeaderName.LOCATION, LOGIN_FAIL_PAGE);
        return HttpResponse.create(StatusCode.FOUND, headers);
    }
}
