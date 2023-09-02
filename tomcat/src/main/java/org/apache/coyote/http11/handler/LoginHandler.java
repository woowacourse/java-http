package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.util.Map;
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
        Headers headers = new Headers(Map.of(
                "Content-Type", ContentTypeParser.parse(absolutePath),
                "Content-Length", String.valueOf(resource.getBytes().length)
        ));
        ResponseBody responseBody = new ResponseBody(resource);

        return Response.from(request.getHttpVersion(), HttpStatus.OK,
                headers, responseBody);
    }

    private Response responseWhenHttpMethodIsPost(Request request) throws IOException {
        RequestBody requestBody = request.getBody();
        String account = requestBody.get("account");
        String password = requestBody.get("password");

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
        Headers headers = new Headers(Map.of(
                "Content-Type", ContentTypeParser.parse(absolutePath),
                "Content-Length", String.valueOf(resource.getBytes().length)
        ));
        ResponseBody responseBody = new ResponseBody(resource);

        return Response.from(request.getHttpVersion(), HttpStatus.UNAUTHORIZED,
                headers, responseBody);
    }

    private Response responseWhenLoginSuccess(Request request) throws IOException {
        String absolutePath = "index.html";

        String resource = findResourceWithPath(absolutePath);
        Headers headers = new Headers(Map.of(
                "Content-Type", ContentTypeParser.parse(absolutePath),
                "Content-Length", String.valueOf(resource.getBytes().length)
        ));
        ResponseBody responseBody = new ResponseBody(resource);

        return Response.from(request.getHttpVersion(), HttpStatus.FOUND,
                headers, responseBody);
    }
}
