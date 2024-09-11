package com.techcourse.controller;

import static org.apache.coyote.http.MediaType.TEXT_HTML;

import org.apache.coyote.http.Cookie;
import org.apache.coyote.http.HttpBody;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpQueryParams;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpResponseBuilder;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.HttpStatusLine;
import org.apache.coyote.http.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public HttpResponse handle(final HttpRequest request) {
        return process(request).build();
    }

    private HttpResponseBuilder process(final HttpRequest request) {
        HttpMethod method = request.getMethod();
        if (method == HttpMethod.GET) {
            return get(request);
        }
        if (method == HttpMethod.POST) {
            return post(request);
        }
        throw new IllegalArgumentException("Unsupported HTTP method: " + method);
    }

    private HttpResponseBuilder get(final HttpRequest request) {
        HttpStatusLine statusLine = new HttpStatusLine(request.getHttpVersion(), HttpStatusCode.OK);
        String resource = StaticResourceHandler.handle("/login.html");
        HttpBody responseBody = new HttpBody(resource);
        return HttpResponse.builder()
                .statusLine(statusLine)
                .body(responseBody)
                .contentType(TEXT_HTML.defaultCharset());
    }

    private HttpResponseBuilder post(final HttpRequest request) {
        if (login(request) != null) {
            HttpStatusLine statusLine = new HttpStatusLine(HttpVersion.HTTP11, HttpStatusCode.FOUND);
            return HttpResponse.builder()
                    .statusLine(statusLine)
                    .contentType(TEXT_HTML.defaultCharset())
                    .setCookie(new Cookie(Cookie.JSESSIONID, "656cef62-e3c4-40bc-a8df-94732920ed46"))
                    .location("/index.html");
        }
        HttpStatusLine statusLine = new HttpStatusLine(HttpVersion.HTTP11, HttpStatusCode.UNAUTHORIZED);
        return HttpResponse.builder()
                .statusLine(statusLine)
                .contentType(TEXT_HTML.defaultCharset())
                .location("/login.html");
    }

    private User login(final HttpRequest request) {
        HttpBody body = request.getBody();
        String content = body.getContent();
        HttpQueryParams query = new HttpQueryParams(content);
        String account = query.get("account");
        String password = query.get("password");
        User user = InMemoryUserRepository.findByAccount(account).orElse(null);
        if ((user != null) && user.checkPassword(password)) {
            log.info("user: {}", user);
            return user;
        }
        return null;
    }
}
