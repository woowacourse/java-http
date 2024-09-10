package org.apache.coyote.http11.handler;

import java.io.IOException;

import org.apache.coyote.http11.RequestHandler;
import org.apache.coyote.http11.exception.CanNotHandleRequest;
import org.apache.coyote.http11.exception.NoSuchUserException;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.response.StaticResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class DefaultResourceHandler implements RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(DefaultResourceHandler.class);

    @Override
    public String handle(Request request) throws IOException {
        log.info("request : {}", request);
        if (request.isStaticResourceRequest()) {
            return fromStaticResource(request.getHttpVersion(), request.getTarget()).toHttpMessage();
        }
        if (request.getTarget().equals("/")) {
            return fromStaticResource(request.getHttpVersion(), "/index.html").toHttpMessage();
        }
        if (request.getTarget().contains("login")) {
            return loginResponse(request);
        }
        if (request.getTarget().contains("register")) {
            return registerResponse(request);
        }
        throw new CanNotHandleRequest("처리할 수 없는 요청입니다. : " + request.getTarget());
    }

    private Response fromStaticResource(String protocolVersion, String path) throws IOException {
        StaticResource staticResource = new StaticResource(path);
        Response response = new Response();
        response.addHeaders("Content-Type", staticResource.getContentType() + ";charset=utf-8");
        response.addContent(staticResource.getContent());
        response.setStatus(protocolVersion, 200, "OK");
        return response;
    }

    private String loginResponse(Request request) throws IOException {
        Response response = new Response();
        if (request.getTarget().contains("?")) {
            QueryParameters queryParams = QueryParameters.parseFrom(request.getTarget().split("\\?")[1]);
            boolean isLogin = login(
                    queryParams.getParam("account"),
                    queryParams.getParam("password")
            );
            if (isLogin) {
                response.addHeaders("Location", "/index.html");
                response.setStatus(request.getHttpVersion(), 301, "FOUND");
                return response.toHttpMessage();
            }
            response.addHeaders("Location", "/401.html");
            response.setStatus(request.getHttpVersion(), 301, "FOUND");

            return response.toHttpMessage();
        }
        return fromStaticResource(request.getHttpVersion(), "/login.html").toHttpMessage();
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
            Response response = new Response();
            response.addHeaders("Location", "/index.html");
            response.setStatus(request.getHttpVersion(), 301, "FOUND");
            return response.toHttpMessage();
        }
        log.info("find resource");
        StaticResource resource = new StaticResource("/register.html");
        log.info("resource = {}", resource);
        return fromStaticResource(request.getHttpVersion(), "/register.html").toHttpMessage();
    }

    private void register(QueryParameters queryParams) {
        InMemoryUserRepository.save(new User(
                queryParams.getParam("account"),
                queryParams.getParam("password"),
                queryParams.getParam("email")
        ));
    }
}
