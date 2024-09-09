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

public class StaticResourceHandler implements RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(StaticResourceHandler.class);

    @Override
    public String handle(Request request) throws IOException {
        log.info("request : {}", request);
        if (request.isStaticResourceRequest()) {
            StaticResource staticResource = new StaticResource(request.getTarget());
            return Response.writeAsStaticResource(request, staticResource.getContentType(),
                    staticResource.getContent());
        }
        if (request.getTarget().equals("/")) {
            StaticResource staticResource = new StaticResource("/index.html");
            return Response.writeAsStaticResource(request, staticResource.getContentType(),
                    staticResource.getContent());
        }
        if (request.getTarget().contains("login")) {
            return loginResponse(request);
        }
        throw new CanNotHandleRequest("처리할 수 없는 요청입니다. : " + request.getTarget());
    }

    private String loginResponse(Request request) throws IOException {
        MethodRequest methodRequest = new MethodRequest(request.getTarget());
        if (request.getTarget().contains("?")) {
            boolean isLogin = login(
                    methodRequest.getParam("account"),
                    methodRequest.getParam("password")
            );
            if (isLogin) {
                return Response.writeAsFound(request, "/index.html");
            }
            return Response.writeAsFound(request, "/401.html");
        }
        StaticResource resource = new StaticResource("/login.html");
        return Response.writeAsStaticResource(request, resource.getContentType(), resource.getContent());
    }

    private boolean login(String account, String password) throws NoSuchUserException {
        User user = InMemoryUserRepository.findByAccount(account).
                orElseThrow(() -> new NoSuchUserException(account + " 에 해당하는 유저를 찾을 수 없습니다."));
        return user.checkPassword(password);
    }
}
