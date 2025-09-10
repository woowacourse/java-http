package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.cookie.ResponseCookie;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpResponseHeader;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.response.StatusLine;

public class LoginController implements Controller {

    private static final String JAVA_SESSION_ID_KEY = "JSESSIONID";

    private final SessionManager sessionManager;

    public LoginController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> parsedRequestBody = request.parseToMap();

        Optional<User> foundUser = InMemoryUserRepository.findByAccount(parsedRequestBody.get("account"));
        HttpResponseHeader httpResponseHeader = new HttpResponseHeader();

        if (foundUser.isPresent() && foundUser.get().checkPassword(parsedRequestBody.get("password"))) {
            ResponseCookie responseCookie = createResponseCookie(request, foundUser.get());
            httpResponseHeader.add("Location", "index.html");
            httpResponseHeader.addCookie(responseCookie);

            StatusLine statusLine = new StatusLine(HttpStatus.FOUND, request);

            response.setStatusLine(statusLine);
            response.setHttpResponseHeader(httpResponseHeader);
            return;
        }

        httpResponseHeader.add("Location", "401.html");

        StatusLine statusLine = new StatusLine(HttpStatus.UNAUTHORIZED, request);
        response.setStatusLine(statusLine);
        response.setHttpResponseHeader(httpResponseHeader);
    }

    private ResponseCookie createResponseCookie(HttpRequest httpRequest, User user) {
        HttpSession session = httpRequest.getSession(sessionManager, true);
        session.setAttribute(session.getId(), user);

        ResponseCookie responseCookie = new ResponseCookie();
        responseCookie.add(JAVA_SESSION_ID_KEY, session.getId());
        return responseCookie;
    }
}
