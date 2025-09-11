package com.techcourse.controller;

import com.techcourse.controller.core.AbstractRequestController;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.http.ContentType;
import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.HttpVersion;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.Location;
import org.apache.coyote.http.response.ResponseBody;

public class RegisterRequestController extends AbstractRequestController {

    private final HttpVersion httpVersion;

    public RegisterRequestController(final HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        return HttpResponse.ok(httpVersion, ContentType.TEXT_HTML, ResponseBody.createBy(httpRequest));
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        Map<String, String> requestBodies = httpRequest.getRequestBody();
        registerUser(requestBodies);

        HttpCookie responseCookie = HttpCookie.empty();
        if (httpRequest.hasEmptySessionId()) {
            responseCookie.addSessionId(String.valueOf(UUID.randomUUID()));
        }
        return HttpResponse.found(httpVersion, new Location("/index.html"), ContentType.APPLICATION_JSON,
                responseCookie);
    }

    private void registerUser(final Map<String, String> requestBodies) {
        String account = requestBodies.get("account");
        String password = requestBodies.get("password");
        String email = requestBodies.get("email");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }
}
