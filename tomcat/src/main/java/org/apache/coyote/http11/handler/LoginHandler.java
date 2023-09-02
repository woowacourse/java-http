package org.apache.coyote.http11.handler;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.ContentTypeParser;
import org.apache.coyote.http11.httpmessage.*;

public class LoginHandler extends Handler {

    public Response handle(Request request) throws IOException {
        HttpMethod httpMethod = request.getMethod();
        if (httpMethod.isEqualTo(HttpMethod.GET)) {
            return responseWhenHttpMethodIsGet(request);
        }
        if (httpMethod.isEqualTo(HttpMethod.POST)) {
            return responseWhenHttpMethodIsPost(request);
        }

        throw new IllegalArgumentException();
    }

    private Response responseWhenHttpMethodIsGet(Request request) throws IOException {
        String absolutePath = "login.html";

        String resource = findResourceWithPath(absolutePath);
        String contentType = ContentTypeParser.parse(absolutePath);
        int contentLength = resource.getBytes().length;

        return Response.from(request.getHttpVersion(), HttpStatus.OK,
                contentType, contentLength, resource);
    }

    private Response responseWhenHttpMethodIsPost(Request request) throws IOException {
        Body body = request.getBody();
        String account = body.get("account");
        String password = body.get("password");

        // TODO: 없는 계정으로 로그인 했을 때에도 responseWhenLoginFail로 이동
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow();

        if (user.hasSameCredential(account, password)) {
            return responseWhenLoginSuccess(request);
        }
        return responseWhenLoginFail(request);
    }

    private Response responseWhenLoginFail(Request request) throws IOException {
        String absolutePath = "401.html";

        String resource = findResourceWithPath(absolutePath);
        String contentType = ContentTypeParser.parse(absolutePath);
        int contentLength = resource.getBytes().length;

        return Response.from(request.getHttpVersion(), HttpStatus.UNAUTHORIZED,
                contentType, contentLength, resource);
    }

    private Response responseWhenLoginSuccess(Request request) throws IOException {
        String absolutePath = "index.html";

        String resource = findResourceWithPath(absolutePath);
        String contentType = ContentTypeParser.parse(absolutePath);
        int contentLength = resource.getBytes().length;

        return Response.from(request.getHttpVersion(), HttpStatus.FOUND,
                contentType, contentLength, resource);
    }
}
