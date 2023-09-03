package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.util.List;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.session.HttpSession;
import org.apache.coyote.Handler;
import org.apache.coyote.common.HttpContentType;
import org.apache.coyote.common.HttpCookieResponse;
import org.apache.coyote.common.HttpMethod;
import org.apache.coyote.common.HttpProtocol;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.common.QueryString;
import org.apache.coyote.exception.MethodNotAllowedException;
import org.apache.coyote.util.QueryParser;
import org.apache.coyote.util.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        HttpMethod httpMethod = request.getHttpMethod();
        if (httpMethod == HttpMethod.GET) {
            return doGet(request);
        }
        if (httpMethod == HttpMethod.POST) {
            return doPost(request);
        }
        throw new MethodNotAllowedException(List.of(HttpMethod.GET, HttpMethod.POST));
    }

    private HttpResponse doGet(HttpRequest request) throws IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            HttpResponse response = new HttpResponse(HttpProtocol.HTTP11, HttpStatus.FOUND);
            response.setHeader("Location", "/index.html");
            return response;
        }
        HttpResponse response = new HttpResponse(HttpProtocol.HTTP11, HttpStatus.OK);
        response.setContentBody(ResourceResolver.resolve("/login.html"));
        response.setContentType(HttpContentType.TEXT_HTML);
        return response;
    }

    private HttpResponse doPost(HttpRequest request) {
        String requestBody = request.getRequestBody();
        QueryString query = QueryParser.parse(requestBody);
        String account = query.get("account");
        String password = query.get("password");
        return InMemoryUserRepository.findByAccount(account)
            .filter(user -> user.checkPassword(password))
            .map(user -> loginSuccess(request, user))
            .orElseGet(this::loginFail);
    }

    private HttpResponse loginSuccess(HttpRequest request, User user) {
        log.info("로그인 성공! 아이디 : {}", user.getAccount());
        HttpSession session = request.getSession(true);
        session.setAttribute("user", user);
        HttpResponse response = new HttpResponse(HttpProtocol.HTTP11, HttpStatus.FOUND);
        response.addHeader("Location", "/index.html");
        response.setCookie(new HttpCookieResponse("JSESSIONID", session.getId()));
        return response;
    }

    private HttpResponse loginFail() {
        HttpResponse response = new HttpResponse(HttpProtocol.HTTP11, HttpStatus.FOUND);
        response.addHeader("Location", "/401.html");
        return response;
    }
}
