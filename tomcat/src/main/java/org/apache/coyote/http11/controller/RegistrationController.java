package org.apache.coyote.http11.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.controller.controllermapping.RequestMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.resource.FileHandler;
import org.apache.coyote.http11.response.HttpResponse;

@RequestMapping(uri = "/register")
public class RegistrationController extends AbstractController {
    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final Map<String, String> requestBody = request.getRequestBodyAsMap();
        final User user = new User(requestBody.get("account"), requestBody.get("password"), requestBody.get("email"));
        InMemoryUserRepository.save(user);

        response.sendRedirect("/index.html");
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        response.setCharSet("utf-8");
        response.setContentType("text/html");
        response.setResponseStatus("200");
        response.setResponseBody(new FileHandler().readFromResourcePath("static/register.html"));
        response.flush();
    }
}
