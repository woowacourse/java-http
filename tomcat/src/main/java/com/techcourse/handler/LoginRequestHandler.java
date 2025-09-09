package com.techcourse.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.NotFoundException;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.http.common.ContentType;
import com.techcourse.http.common.HttpCookie;
import com.techcourse.http.common.HttpMethod;
import com.techcourse.http.common.HttpVersion;
import com.techcourse.http.request.HttpRequest;
import com.techcourse.http.response.HttpResponse;
import com.techcourse.http.response.Location;
import com.techcourse.http.response.ResponseBody;
import com.techcourse.http.session.Session;
import com.techcourse.http.session.SessionRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;

public class LoginRequestHandler {

    private final HttpVersion httpVersion;

    public LoginRequestHandler(final HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    public HttpResponse handleLoginRequest(final HttpRequest httpRequest) {
        HttpMethod httpMethod = httpRequest.getHttpMethod();

        if (httpMethod == HttpMethod.GET) {
            return handleGetHttpMethod(httpRequest);
        }
        if (httpMethod == HttpMethod.POST) {
            return handlePostHttpMethod(httpRequest);
        }

        throw new UncheckedServletException("지원하지 않는 Http Method 입니다.");
    }

    private HttpResponse handleGetHttpMethod(final HttpRequest httpRequest) {
        if (!httpRequest.hasEmptySessionId()) {
            return createGetLoginResponseBySession(httpRequest);
        }
        return HttpResponse.ok(httpVersion, ContentType.TEXT_HTML, HttpCookie.empty(),
                ResponseBody.createBy(httpRequest));
    }

    private HttpResponse createGetLoginResponseBySession(final HttpRequest httpRequest) {
        Optional<Session> session = SessionRepository.findById(httpRequest.getJSessionId());

        if (session.isPresent()) {
            return HttpResponse.found(httpVersion, new Location("/index.html"), HttpCookie.empty());
        }
        return HttpResponse.ok(httpVersion, ContentType.TEXT_HTML, HttpCookie.empty(),
                ResponseBody.createBy(httpRequest));
    }

    private HttpResponse handlePostHttpMethod(final HttpRequest httpRequest) {
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
            return HttpResponse.found(httpVersion, new Location("/index.html"), httpCookie);
        }
        return HttpResponse.found(httpVersion, new Location("/401.html"), httpCookie);
    }
}
