package org.apache.coyote.http11.servlet;

import static org.apache.coyote.http11.PagePathMapper.*;
import static org.apache.coyote.http11.message.HttpHeaders.*;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.message.Headers;
import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.RequestBody;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.ResponseBody;

public class RegisterServlet extends Servlet {

    @Override
    public HttpResponse service(HttpRequest httpRequest) throws IOException {
        if (httpRequest.getMethod().isEqualTo(HttpMethod.GET)) {
            return doGet(httpRequest);
        }
        if (httpRequest.getMethod().isEqualTo(HttpMethod.POST)) {
            return doPost(httpRequest);
        }

        throw new IllegalArgumentException();
    }

    private HttpResponse doGet(HttpRequest httpRequest) throws IOException {
        String absolutePath = REGISTER_PAGE.path();

        String resource = findResourceWithPath(absolutePath);
        Headers headers = Headers.fromMap(Map.of(
                CONTENT_TYPE, ContentType.parse(absolutePath),
                CONTENT_LENGTH, String.valueOf(resource.getBytes().length)
        ));
        ResponseBody responseBody = new ResponseBody(resource);

        return new HttpResponse(httpRequest.getHttpVersion(), HttpStatus.OK,
                headers, responseBody);
    }

    private HttpResponse doPost(HttpRequest httpRequest) {
        saveUser(httpRequest);
        String absolutePath = INDEX_PAGE.path();

        Headers headers = Headers.fromMap(Map.of(
                LOCATION, absolutePath
        ));

        return new HttpResponse(httpRequest.getHttpVersion(), HttpStatus.FOUND,
                headers, ResponseBody.ofEmpty());
    }

    private void saveUser(HttpRequest httpRequest) {
        RequestBody requestBody = httpRequest.getBody();
        Map<String, String> formData = requestBody.getAsFormData();

        String account = formData.get("account");
        String password = formData.get("password");
        String email = formData.get("email");
        User user = new User(account, password, email);

        InMemoryUserRepository.save(user);
    }
}
