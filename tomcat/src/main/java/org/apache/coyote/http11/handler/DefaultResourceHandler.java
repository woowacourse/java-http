package org.apache.coyote.http11.handler;

import java.io.IOException;

import org.apache.coyote.http11.RequestHandler;
import org.apache.coyote.http11.exception.CanNotHandleRequest;
import org.apache.coyote.http11.exception.NoSuchUserException;
import org.apache.coyote.http11.httpmessage.request.Request;
import org.apache.coyote.http11.httpmessage.response.Response;
import org.apache.coyote.http11.httpmessage.response.StaticResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        if (request.getTarget().contains("?")) {
            QueryParameters queryParams = QueryParameters.parseFrom(request.getTarget().split("\\?")[1]);
            boolean isLogin = login(
                    queryParams.getParam("account"),
                    queryParams.getParam("password")
            );
            if (isLogin) {
                return Response.builder()
                        .versionOf(request.getHttpVersion())
                        .found("/index.html")
                        .toHttpMessage();
            }
            return Response.builder()
                    .versionOf(request.getHttpVersion())
                    .found("/401.html")
                    .toHttpMessage();
        }

        return Response.builder()
                .versionOf(request.getHttpVersion())
                .ofStaticResource(new StaticResource("/login.html"))
                .toHttpMessage();
    }

    private boolean login(String account, String password) throws NoSuchUserException {
        User user = InMemoryUserRepository.findByAccount(account).
                orElseThrow(() -> new NoSuchUserException(account + " 에 해당하는 유저를 찾을 수 없습니다."));
        return user.checkPassword(password);
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
