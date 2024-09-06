package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.catalina.view.View;
import org.apache.catalina.view.ViewResolver;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        Session session = extractSession(request);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                responseLoginSuccess(response, session);
                return;
            }
        }
        responseLoginPage(response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String requestBody = request.getRequestBody();
        if (requestBody == null) {
            throw new IllegalArgumentException("Query string is missing in the request");
        }

        Map<String, String> requestForm = extractFormData(requestBody);
        String userName = requestForm.get("account");
        String password = requestForm.get("password");

        Optional<User> account = InMemoryUserRepository.findByAccount(userName);
        if (account.isEmpty()) {
            responseLoginFail(response);
        } else {
            User user = account.get();
            if (user.checkPassword(password)) {
                Session session = saveSession(user);
                responseLoginSuccess(response, session);
            } else {
                responseLoginFail(response);
            }
        }
    }

    private Session extractSession(HttpRequest request) {
        String cookie = request.getHeader("Cookie");
        Map<String, String> cookies = new HashMap<>();
        for (String cookieParts : cookie.split(" ")) {
            String[] keyAndValue = cookieParts.split("=");
            cookies.put(keyAndValue[0], keyAndValue[1]);
        }

        String jsessionId = cookies.get("JSESSIONID");
        SessionManager sessionManager = new SessionManager();
        return sessionManager.findSession(jsessionId);
    }

    private Session saveSession(User user) {
        Session session = new Session();
        session.setAttribute("user", user);
        SessionManager sessionManager = new SessionManager();
        sessionManager.add(session);
        return session;
    }

    private Map<String, String> extractFormData(String requestBody) {
        Map<String, String> requestData = new HashMap<>();
        String[] keyValuePairs = requestBody.split("&");
        for (String keyValuePair : keyValuePairs) {
            String[] keyValue = keyValuePair.split("=");
            requestData.put(keyValue[0], keyValue[1]);
        }

        return requestData;
    }

    private void responseLoginPage(HttpResponse response) throws IOException {
        View view = ViewResolver.getView("/login.html");
        response.setStatus200();
        response.setResponseBody(view.getContent());
        response.setContentTypeHtml();
    }

    private void responseLoginSuccess(HttpResponse response, Session session) {
        response.setStatus302();
        response.setLocation("/index.html");
        response.setCookie(HttpCookie.ofJSessionId(session.getId()));
    }

    private void responseLoginFail(HttpResponse response) {
        response.setStatus302();
        response.setLocation("/401.html");
    }
}
