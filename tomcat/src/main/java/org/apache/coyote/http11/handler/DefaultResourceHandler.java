package org.apache.coyote.http11.handler;

import java.io.IOException;

import org.apache.coyote.http11.RequestHandler;
import org.apache.coyote.http11.exception.CanNotHandleRequest;
import org.apache.coyote.http11.exception.NoSuchUserException;
import org.apache.coyote.http11.httpmessage.request.Request;
import org.apache.coyote.http11.httpmessage.response.Response;
import org.apache.coyote.http11.httpmessage.response.StaticResource;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class DefaultResourceHandler implements RequestHandler {

    @Override
    public String handle(Request request) throws IOException {
        if (request.isStaticResourceRequest()) {
            return Response.builder()
                    .versionOf(request.getHttpVersion())
                    .ofStaticResource(new StaticResource(request.getTarget()))
                    .toHttpMessage();
        }
        if (request.getTarget().equals("/")) {
            return Response.builder()
                    .versionOf(request.getHttpVersion())
                    .ofStaticResource(new StaticResource("/index.html"))
                    .toHttpMessage();
        }
        if (request.getTarget().contains("login")) {
            return loginResponse(request);
        }
        if (request.getTarget().contains("register")) {
            return registerResponse(request);
        }
        throw new CanNotHandleRequest("처리할 수 없는 요청입니다. : " + request.getTarget());
    }

    private String loginResponse(Request request) throws IOException {
        if (request.isPost()) {
            return login(request).toHttpMessage();
        }

        return Response.builder()
                .versionOf(request.getHttpVersion())
                .ofStaticResource(new StaticResource("/login.html"))
                .toHttpMessage();
    }

    private Response login(Request request) throws NoSuchUserException {
        QueryParameters queryParams = QueryParameters.parseFrom(request.getBody());
        String account = queryParams.getParam("account");
        String password = queryParams.getParam("password");
        User user = InMemoryUserRepository.findByAccount(account).
                orElseThrow(() -> new NoSuchUserException(account + " 에 해당하는 유저를 찾을 수 없습니다."));

        if (user.checkPassword(password)) {
            Session session = request.getSession(true);
            session.setAttribute("user", user);
            SessionManager.getInstance().add(session);
            return Response.builder()
                    .versionOf(request.getHttpVersion())
                    .addCookie("JSESSIONID", session.getId())
                    .found("/index.html");
        }

        return Response.builder()
                .versionOf(request.getHttpVersion())
                .found("/401.html");
    }

    private String registerResponse(Request request) throws IOException {
        if (request.isPost()) {
            QueryParameters methodRequest = QueryParameters.parseFrom(request.getBody());
            register(methodRequest);
            return Response.builder()
                    .versionOf(request.getHttpVersion())
                    .found("/index.html")
                    .toHttpMessage();
        }

        return Response.builder()
                .versionOf(request.getHttpVersion())
                .ofStaticResource(new StaticResource("/register.html"))
                .toHttpMessage();
    }

    private void register(QueryParameters queryParams) {
        InMemoryUserRepository.save(new User(
                queryParams.getParam("account"),
                queryParams.getParam("password"),
                queryParams.getParam("email")
        ));
    }
}