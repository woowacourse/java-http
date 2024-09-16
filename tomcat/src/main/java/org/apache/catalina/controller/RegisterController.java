package org.apache.catalina.controller;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StatusLine;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(final Request request, final Response response) throws Exception {
        final RequestBody requestBody = request.getBody();
        final User user = new User(
                requestBody.getByName("account"),
                requestBody.getByName("password"),
                requestBody.getByName("email")
        );
        InMemoryUserRepository.save(user);

        response.setStatusLine(new StatusLine("HTTP/1.1", "302", "Found"));
        response.addLocation("index.html");
    }

    @Override
    protected void doGet(final Request request, final Response response) throws Exception {
        response.setStatusLine(new StatusLine("HTTP/1.1", "200", "OK"));
        response.setBody(ResponseBody.form(request.getRequestLine()));
        response.addContentType();
        response.addContentLength();
    }
}
