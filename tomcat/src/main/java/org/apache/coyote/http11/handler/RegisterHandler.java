package org.apache.coyote.http11.handler;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.ContentTypeParser;
import org.apache.coyote.http11.httpmessage.*;

public class RegisterHandler extends Handler {

    @Override
    public Response handle(Request request) throws IOException {
        if (request.getMethod().isEqualTo(HttpMethod.GET)) {
            return responseWhenHttpMethodIsGet(request);
        }
        if (request.getMethod().isEqualTo(HttpMethod.POST)) {
            return responseWhenHttpMethodIsPost(request);
        }

        throw new IllegalArgumentException();
    }

    private Response responseWhenHttpMethodIsGet(Request request) throws IOException {
        String target = "register.html";

        String resource = findResourceWithPath(target);
        String contentType = ContentTypeParser.parse(target);
        int contentLength = resource.getBytes().length;

        return Response.from(request.getHttpVersion(), HttpStatus.OK,
                contentType, contentLength, resource);
    }

    private Response responseWhenHttpMethodIsPost(Request request) throws IOException {
        saveUser(request);

        String absolutePath = "index.html";
        String resource = findResourceWithPath(absolutePath);
        String contentType = ContentTypeParser.parse(absolutePath);
        int contentLength = resource.getBytes().length;

        return Response.from(request.getHttpVersion(), HttpStatus.FOUND,
                contentType, contentLength, resource);
    }

    private void saveUser(Request request) {
        Body body = request.getBody();
        String account = body.get("account");
        String password = body.get("password");
        String email = body.get("email");
        User user = new User(account, password, email);

        InMemoryUserRepository.save(user);
    }
}
