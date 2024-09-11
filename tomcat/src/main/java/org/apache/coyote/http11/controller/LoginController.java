package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.UUID;
import org.apache.coyote.http11.HttpHeaderName;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.httprequest.HttpCookie;
import org.apache.coyote.http11.httprequest.HttpCookieConvertor;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String LOGIN_PATH = "/login";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String INDEX_PATH = "/index.html";
    private static final String UNAUTHORIZED_PATH = "/401.html";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE_DELIMITER = "JSESSIONID";

    private final Session session = Session.getInstance();

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) {
        String account = httpRequest.getBodyValue(ACCOUNT);
        String password = httpRequest.getBodyValue(PASSWORD);

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow();
        UUID uuid = UUID.randomUUID();

        if (user.checkPassword(password)) {
            session.save(uuid.toString(), user);
            log.info(user.toString());
            return HttpResponse.found(httpRequest)
                    .setCookie(JSESSIONID + COOKIE_DELIMITER + uuid)
                    .location(INDEX_PATH)
                    .build();
        }
        log.error("비밀번호 불일치");
        return redirectPage(httpRequest, UNAUTHORIZED_PATH);
    }

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) {
        if (!httpRequest.containsHeader(HttpHeaderName.COOKIE)) {
            return redirectPage(httpRequest, LOGIN_PATH);
        }

        HttpCookie httpCookie = HttpCookieConvertor.convertHttpCookie(
                httpRequest.getHeaderValue(HttpHeaderName.COOKIE));
        if (!httpCookie.containsCookie(JSESSIONID)) {
            return redirectPage(httpRequest, LOGIN_PATH);
        }

        String jsessionid = httpCookie.getCookieValue(JSESSIONID);
        if (!session.containsUser(jsessionid)) {
            return redirectPage(httpRequest,LOGIN_PATH);
        }

        User user = session.getUser(jsessionid);
        log.info(user.toString());
        return HttpResponse.found(httpRequest)
                .setCookie(JSESSIONID + COOKIE_DELIMITER + jsessionid)
                .location(INDEX_PATH)
                .build();
    }

    private HttpResponse redirectPage(HttpRequest httpRequest, String path) {
        return HttpResponse.found(httpRequest)
                .location(path)
                .build();
    }
}
