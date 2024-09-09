package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

import org.apache.coyote.http11.RequestHandler;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.StaticResource;
import org.apache.coyote.http11.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class StaticResourceHandler implements RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(StaticResourceHandler.class);

    @Override
    public String handle(Request request) throws IOException {
        try {
            StaticResource staticResource = getContent(request);
            return Response.writeResponse(request, staticResource.getContentType(), staticResource.getContent());
        } catch (NoSuchFileException e) {
            return null;
        }
    }

    private StaticResource getContent(Request request) throws IOException {
        String target = request.getTarget().equals("/") ? "index.html" : request.getTarget();
        if (target.contains("login")) {
            return loginResponse(request);
        }
        return new StaticResource(target);
    }

    private StaticResource loginResponse(Request request) throws IOException {
        MethodRequest methodRequest = new MethodRequest(request.getTarget());
        if (request.getTarget().contains("?")) {
            checkLogin(methodRequest.getParam("account"));
        }
        return new StaticResource(methodRequest.getEndPoint() + ".html");
    }

    private void checkLogin(String account) {
        User user = InMemoryUserRepository.findByAccount(account).get();
        log.info("User : {}", user);
    }
}
