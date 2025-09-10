package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.cookie.ResponseCookie;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpResponseHeader;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.response.StatusLine;

public class RegisterController implements Controller {

    private static final String JAVA_SESSION_ID_KEY = "JSESSIONID";

    private final SessionManager sessionManager;

    public RegisterController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        String requestBody = request.getRequestBody();

        Map<String, String> parsedRequestBody = parseRequestBody(requestBody);
        User user = new User(parsedRequestBody.get("account"), parsedRequestBody.get("password"),
                parsedRequestBody.get("email"));
        InMemoryUserRepository.save(user);

        ResponseCookie responseCookie = getCookie(request, user);
        StatusLine statusLine = new StatusLine(HttpStatus.FOUND, request);

        HttpResponseHeader httpResponseHeader = new HttpResponseHeader();
        httpResponseHeader.add("Location", "index.html");
        httpResponseHeader.addCookie(responseCookie);

        response.setStatusLine(statusLine);
        response.setHttpResponseHeader(httpResponseHeader);
    }

    private Map<String, String> parseRequestBody(String requestBody) {
        Map<String, String> parsedRequestBody = new HashMap<>();

        String[] pairs = requestBody.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            String key = keyValue[0];
            String value = keyValue[1];
            parsedRequestBody.put(key, value);
        }

        return parsedRequestBody;
    }

    private ResponseCookie getCookie(HttpRequest httpRequest, User user) {
        HttpSession session = httpRequest.getSession(sessionManager, true);
        session.setAttribute(session.getId(), user);

        ResponseCookie responseCookie = new ResponseCookie();
        responseCookie.add(JAVA_SESSION_ID_KEY, session.getId());
        return responseCookie;
    }
}
