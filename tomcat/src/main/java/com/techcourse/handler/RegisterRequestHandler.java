package com.techcourse.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.http.ContentType;
import com.techcourse.http.HttpCookie;
import com.techcourse.http.HttpVersion;
import com.techcourse.http.request.HttpRequest;
import com.techcourse.http.response.HttpResponse;
import com.techcourse.http.response.Location;
import com.techcourse.http.response.ResponseBody;
import com.techcourse.model.User;
import java.util.Map;
import java.util.UUID;

public class RegisterRequestHandler extends AbstractRequestHandler {

    private final HttpVersion httpVersion;

    public RegisterRequestHandler(final HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        return HttpResponse.ok(httpVersion, ContentType.TEXT_HTML, HttpCookie.empty(),
                ResponseBody.createBy(httpRequest));
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
