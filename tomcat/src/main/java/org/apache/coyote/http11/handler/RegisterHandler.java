package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.PagePathMapper.*;
import static org.apache.coyote.http11.message.HttpHeaders.*;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.ContentTypeParser;
import org.apache.coyote.http11.message.Headers;
import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.request.Request;
import org.apache.coyote.http11.message.request.RequestBody;
import org.apache.coyote.http11.message.response.Response;
import org.apache.coyote.http11.message.response.ResponseBody;

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
        String absolutePath = REGISTER_PAGE.path();

        String resource = findResourceWithPath(absolutePath);
        Headers headers = Headers.fromMap(Map.of(
                CONTENT_TYPE, ContentTypeParser.parse(absolutePath),
                CONTENT_LENGTH, String.valueOf(resource.getBytes().length)
        ));
        ResponseBody responseBody = new ResponseBody(resource);

        return Response.from(request.getHttpVersion(), HttpStatus.OK,
                headers, responseBody);
    }

    private Response responseWhenHttpMethodIsPost(Request request) {
        saveUser(request);
        String absolutePath = INDEX_PAGE.path();

        Headers headers = Headers.fromMap(Map.of(
                LOCATION, absolutePath
        ));

        return Response.from(request.getHttpVersion(), HttpStatus.FOUND,
                headers, ResponseBody.ofEmpty());
    }

    private void saveUser(Request request) {
        RequestBody requestBody = request.getBody();
        String account = requestBody.get("account");
        String password = requestBody.get("password");
        String email = requestBody.get("email");
        User user = new User(account, password, email);

        InMemoryUserRepository.save(user);
    }
}
