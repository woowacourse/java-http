package org.apache.coyote.http11.handler;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpHeaderName;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.Session;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.QueryParams;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;
import org.apache.coyote.http11.util.Parser;
import org.apache.coyote.http11.util.StaticFileLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public HttpResponse handle(final HttpRequest request) throws IOException {
        if (request.getMethod() == HttpMethod.GET) {
            if (isLoggedIn(request)) {
                HttpHeaders headers = new HttpHeaders();
                headers.addHeader(HttpHeaderName.LOCATION, "/index.html");
                return HttpResponse.create(StatusCode.FOUND, headers);
            }
            String content = StaticFileLoader.load("/login.html");
            HttpHeaders headers = new HttpHeaders();
            headers.addHeader(HttpHeaderName.CONTENT_TYPE, ContentType.TEXT_HTML.getDetail());
            headers.addHeader(HttpHeaderName.CONTENT_LENGTH, String.valueOf(content.getBytes().length));
            return HttpResponse.create(StatusCode.OK, headers, content);
        }
        if (request.getMethod() == HttpMethod.POST) {
            QueryParams params = Parser.parseToQueryParams(request.getBody().getContent());
            String account = params.getParam("account");
            String password = params.getParam("password");

            return InMemoryUserRepository.findByAccount(account)
                    .filter(user -> user.checkPassword(password))
                    .map(user -> loginSuccess(request, user))
                    .orElseGet(() -> loginFail());
        }
        return null;
    }

    private boolean isLoggedIn(HttpRequest request) {
        User userInSession = (User) request.getSession().getAttribute("user"); // TODO : 세션 새로만들었을 경우..?
        if (userInSession == null) {
            return false;
        }
        return InMemoryUserRepository.findByAccount(userInSession.getAccount())
                .filter(user -> user.checkPassword(userInSession.getPassword()))
                .isPresent();
    }

    private HttpResponse loginSuccess(HttpRequest request, User user) {
        log.info("로그인 성공 : {}", user.toString());
        Session session = request.getSession();
        session.setAttribute("user", user);

        HttpHeaders headers = new HttpHeaders();
        headers.addHeader(HttpHeaderName.LOCATION, "/index.html");
        headers.addHeader(HttpHeaderName.SET_COOKIE, "JSESSIONID=" + session.getId());
        return HttpResponse.create(StatusCode.FOUND, headers);
    }

    private HttpResponse loginFail() {
        HttpHeaders headers = new HttpHeaders();
        headers.addHeader(HttpHeaderName.LOCATION, "/401.html");
        return HttpResponse.create(StatusCode.FOUND, headers);
    }
}
