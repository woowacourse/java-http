package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.ContentTypeParser;
import org.apache.coyote.http11.httpmessage.Request;
import org.apache.coyote.http11.httpmessage.RequestURI;
import org.apache.coyote.http11.httpmessage.Response;

public class LoginHandler extends Handler {

    public Response handle(Request request) throws IOException {
        RequestURI requestURI = request.getRequestURI();

        if (!requestURI.hasQueryParameters()) {
            return responseWhenNotLogin(request);
        }

        Map<String, String> queryParameters = requestURI.queryParameters();

        String account = queryParameters.get("account");
        String password = queryParameters.get("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow();

        if (user.hasSameCredential(account, password)) {
            return responseWhenLoginSuccess(request);
        }
        return responseWhenLoginFail(request);
    }

    private Response responseWhenNotLogin(Request request) throws IOException {
        String absolutePath = "login.html";

        String resource = findResourceWithPath(absolutePath);
        String contentType = ContentTypeParser.parse(absolutePath);
        int contentLength = resource.getBytes().length;

        return Response.from(request.getHttpVersion(), "200 UNAUTHORIZED",
                contentType, contentLength, resource);
    }

    private Response responseWhenLoginFail(Request request) throws IOException {
        String absolutePath = "401.html";

        String resource = findResourceWithPath(absolutePath);
        String contentType = ContentTypeParser.parse(absolutePath);
        int contentLength = resource.getBytes().length;

        return Response.from(request.getHttpVersion(), "401 UNAUTHORIZED",
                contentType, contentLength, resource);
    }

    private Response responseWhenLoginSuccess(Request request) throws IOException {
        String absolutePath = "index.html";

        String resource = findResourceWithPath(absolutePath);
        String contentType = ContentTypeParser.parse(absolutePath);
        int contentLength = resource.getBytes().length;

        return Response.from(request.getHttpVersion(), "302 FOUND",
                contentType, contentLength, resource);
    }
}
