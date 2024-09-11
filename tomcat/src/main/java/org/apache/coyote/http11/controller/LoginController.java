package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
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

    private final Session session = Session.getInstance();

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) {
        String account = httpRequest.getBodyValue("account");
        String password = httpRequest.getBodyValue("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow();
        UUID uuid = UUID.randomUUID();

        if (user.checkPassword(password)) {
            session.save(uuid.toString(), user);
            log.info(user.toString());
            return HttpResponse.found(httpRequest)
                    .setCookie("JSESSIONID=" + uuid)
                    .location("/index.html")
                    .build();
        }
        log.error("비밀번호 불일치");
        return HttpResponse.found(httpRequest)
                .location("/401.html")
                .build();
    }

    private boolean checkToken(String[] token) {
        return Arrays.stream(token).anyMatch(t -> t.split("=").length < 2);
    }

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) {
        try {
            if (!httpRequest.containsHeader(HttpHeaderName.COOKIE)) {
                return redirectLoginPage(httpRequest);
            }

            HttpCookie httpCookie = HttpCookieConvertor.convertHttpCookie(httpRequest.getHeaderValue(HttpHeaderName.COOKIE));
            if (!httpCookie.containsKey("JSESSIONID")) {
                return redirectLoginPage(httpRequest);
            }

            String jsessionid = httpCookie.getValue("JSESSIONID");
            if (!session.containsUser(jsessionid)) {
                return redirectLoginPage(httpRequest);
            }

            User user = session.getUser(jsessionid);
            log.info(user.toString());
            return HttpResponse.found(httpRequest)
                    .setCookie("JSESSIONID=" + jsessionid)
                    .location("/index.html")
                    .build();
        } catch (URISyntaxException | IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private HttpResponse redirectLoginPage(HttpRequest httpRequest) throws IOException, URISyntaxException {
        return HttpResponse.ok(httpRequest)
                .staticResource(LOGIN_PATH)
                .build();
    }
}
