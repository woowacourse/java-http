package org.apache.coyote.http11.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.common.HttpContentType;
import org.apache.coyote.common.HttpMethod;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.common.QueryString;
import org.apache.coyote.util.QueryParser;
import org.apache.coyote.util.ResourceResolver;

public class RegisterController extends HttpMethodController {

    public RegisterController() {
        handlers.put(HttpMethod.GET, this::doGet);
        handlers.put(HttpMethod.POST, this::doPost);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setContentBody(ResourceResolver.resolve("/register.html"));
        response.setContentType(HttpContentType.TEXT_HTML);
        response.setHttpStatus(HttpStatus.OK);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String requestBody = request.getRequestBody();
        QueryString query = QueryParser.parse(requestBody);
        User user = getUser(query);
        InMemoryUserRepository.save(user);
        response.sendRedirect("/index.html");
    }

    private User getUser(QueryString query) {
        String account = query.get("account");
        String password = query.get("password");
        String email = query.get("email");
        return new User(account, password, email);
    }
}
