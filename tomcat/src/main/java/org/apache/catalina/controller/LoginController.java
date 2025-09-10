package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.cookie.ResponseCookie;
import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.message.HttpRequest;
import org.apache.coyote.http11.message.HttpResponse;
import org.apache.coyote.http11.message.HttpResponseHeader;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.StatusLine;

public class LoginController implements Controller {

    private static final String JAVA_SESSION_ID_KEY = "JSESSIONID";

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {

        if (request.getHttpMethod().equals(HttpMethod.POST)) {
            doPost(request, response);
        }

        if (request.getHttpMethod().equals(HttpMethod.GET)) {
            doGet(request, response);
        }
    }

    private void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> parsedRequestBody = parseRequestBody(request.getRequestBody());

        Optional<User> foundUser = InMemoryUserRepository.findByAccount(parsedRequestBody.get("account"));
        HttpResponseHeader httpResponseHeader = new HttpResponseHeader();

        if (foundUser.isPresent() && foundUser.get().checkPassword(parsedRequestBody.get("password"))) {
            ResponseCookie responseCookie = getCookie(request, foundUser.get());
            httpResponseHeader.add("Location", "index.html");
            httpResponseHeader.addCookie(responseCookie);

            StatusLine statusLine = new StatusLine(HttpStatus.FOUND, request);

            response = new HttpResponse(statusLine, httpResponseHeader, null);
        }

        httpResponseHeader.add("Location", "401.html");

        StatusLine statusLine = new StatusLine(HttpStatus.UNAUTHORIZED, request);
        response = new HttpResponse(statusLine, httpResponseHeader, null);
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
