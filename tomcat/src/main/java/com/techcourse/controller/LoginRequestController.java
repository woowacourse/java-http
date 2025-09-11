package com.techcourse.controller;

import com.techcourse.controller.core.AbstractRequestController;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.NotFoundException;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http.ContentType;
import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.HttpVersion;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.Location;
import org.apache.coyote.http.response.ResponseBody;
import org.apache.coyote.http.session.Session;
import org.apache.coyote.http.session.SessionRepository;

public class LoginRequestController extends AbstractRequestController {

    private final HttpVersion httpVersion;

    public LoginRequestController(final HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        if (!httpRequest.hasEmptySessionId()) {
            return createGetLoginResponseBySession(httpRequest);
        }
        return HttpResponse.ok(httpVersion, ContentType.TEXT_HTML, HttpCookie.empty(),
                ResponseBody.createBy(httpRequest));
    }

    private HttpResponse createGetLoginResponseBySession(final HttpRequest httpRequest) {
        Optional<Session> session = SessionRepository.findById(httpRequest.getJSessionId());

        if (session.isPresent()) {
            return HttpResponse.found(httpVersion, new Location("/index.html"), ContentType.APPLICATION_JSON,
                    HttpCookie.empty());
        }
        return HttpResponse.ok(httpVersion, ContentType.TEXT_HTML, HttpCookie.empty(),
                ResponseBody.createBy(httpRequest));
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        Map<String, String> requestBody = httpRequest.getRequestBody();

        String account = requestBody.get("account");
        String password = requestBody.get("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));

        return createPostLoginResponse(user, password);
    }

    private HttpResponse createPostLoginResponse(final User user, final String password) {
        HttpCookie httpCookie = HttpCookie.empty();
        if (user.checkPassword(password)) {
            Session session = Session.newSession();
            session.setAttribute("user", user);
            SessionRepository.save(session);
            httpCookie.addSessionId(session.getId());
            return HttpResponse.found(httpVersion, new Location("/index.html"), ContentType.APPLICATION_JSON,
                    httpCookie);
        }
        return HttpResponse.found(httpVersion, new Location("/401.html"), ContentType.APPLICATION_JSON, httpCookie);
    }
}
