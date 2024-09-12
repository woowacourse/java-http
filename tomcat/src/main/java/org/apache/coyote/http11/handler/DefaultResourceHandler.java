package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.util.Objects;

import org.apache.coyote.http11.RequestHandler;
import org.apache.coyote.http11.exception.CanNotHandleRequest;
import org.apache.coyote.http11.exception.NoSuchUserException;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.request.HttpRequestParameters;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.httpmessage.response.StaticResource;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class DefaultResourceHandler implements RequestHandler {

    @Override
    public String handle(HttpRequest httpRequest) throws IOException {
        if (httpRequest.isStaticResourceRequest()) {
            return HttpResponse.builder()
                    .versionOf(httpRequest.getHttpVersion())
                    .ofStaticResource(new StaticResource(httpRequest.getTarget()))
                    .toHttpMessage();
        }
        if (httpRequest.getTarget().equals("/")) {
            return HttpResponse.builder()
                    .versionOf(httpRequest.getHttpVersion())
                    .ofStaticResource(new StaticResource("/index.html"))
                    .toHttpMessage();
        }
        if (httpRequest.getTarget().equals("/login")) {
            return loginResponse(httpRequest);
        }
        if (httpRequest.getTarget().contains("register")) {
            return registerResponse(httpRequest);
        }
        throw new CanNotHandleRequest("처리할 수 없는 요청입니다. : " + httpRequest.getTarget());
    }

    private String loginResponse(HttpRequest httpRequest) throws IOException {
        if (httpRequest.isPost()) {
            return login(httpRequest).toHttpMessage();
        }
        if (isLoggedIn(httpRequest)) {
            return HttpResponse.builder()
                    .versionOf(httpRequest.getHttpVersion())
                    .found("/index.html")
                    .toHttpMessage();
        }
        return HttpResponse.builder()
                .versionOf(httpRequest.getHttpVersion())
                .ofStaticResource(new StaticResource("/login.html"))
                .toHttpMessage();
    }

    private boolean isLoggedIn(HttpRequest httpRequest) {
        return Objects.nonNull(httpRequest.getSession(false));
    }

    private HttpResponse login(HttpRequest httpRequest) throws NoSuchUserException {
        HttpRequestParameters requestParams = HttpRequestParameters.parseFrom(httpRequest.getBody());
        String account = requestParams.getParam("account");
        String password = requestParams.getParam("password");
        User user = InMemoryUserRepository.fetchByAccount(account);
        if (user.checkPassword(password)) {
            Session session = httpRequest.getSession(true);
            session.setAttribute("user", user);
            SessionManager.getInstance().add(session);
            return HttpResponse.builder()
                    .versionOf(httpRequest.getHttpVersion())
                    .addCookie("JSESSIONID", session.getId())
                    .found("/index.html");
        }

        return HttpResponse.builder()
                .versionOf(httpRequest.getHttpVersion())
                .found("/401.html");
    }

    private String registerResponse(HttpRequest httpRequest) throws IOException {
        if (httpRequest.isPost()) {
            HttpRequestParameters methodRequest = HttpRequestParameters.parseFrom(httpRequest.getBody());
            User user = register(methodRequest);
            Session session = httpRequest.getSession(true);
            session.setAttribute("user", user);
            SessionManager.getInstance().add(session);
            return HttpResponse.builder()
                    .versionOf(httpRequest.getHttpVersion())
                    .addCookie("JSESSIONID", session.getId())
                    .found("/index.html")
                    .toHttpMessage();
        }

        return HttpResponse.builder()
                .versionOf(httpRequest.getHttpVersion())
                .ofStaticResource(new StaticResource("/register.html"))
                .toHttpMessage();
    }

    private User register(HttpRequestParameters requestParams) {
        String account = requestParams.getParam("account");
        User user = new User(
                account,
                requestParams.getParam("password"),
                requestParams.getParam("email")
        );
        InMemoryUserRepository.save(user);
        return InMemoryUserRepository.fetchByAccount(account);
    }
}
