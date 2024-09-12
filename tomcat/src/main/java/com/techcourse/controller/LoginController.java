package com.techcourse.controller;

import static org.apache.coyote.http.MediaType.TEXT_HTML;

import org.apache.catalina.session.Session;
import org.apache.coyote.http.HttpBody;
import org.apache.coyote.http.HttpCookie;
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
        HttpBody body = request.getBody();
        String content = body.getContent();
        HttpQueryParams queryParams = new HttpQueryParams(content);
        String account = queryParams.get("account");
        String password = queryParams.get("password");
        User user = InMemoryUserRepository.findByAccount(account).orElse(null);
        if ((user != null) && user.checkPassword(password)) {
            log.info("user: {}", user);
            Session session = request.getSession();
            session.setAttribute("user", user);
            return HttpResponse.builder()
                    .setCookie(HttpCookie.ofJSessionId(session.getId()))
                    .redirect("/index.html");
        }
        HttpStatusLine statusLine = new HttpStatusLine(HttpVersion.HTTP11, HttpStatusCode.UNAUTHORIZED);
        return HttpResponse.builder()
                .statusLine(statusLine)
                .contentType(TEXT_HTML.defaultCharset())
                .location("/login.html");
    }
}
