package org.apache.coyote.http11.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.request_response.HttpCookie;
import org.apache.coyote.http11.request_response.HttpRequest;
import org.apache.coyote.http11.request_response.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.UnAuthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    public boolean supports(HttpRequest request) {
        return request.getRequestMethod().equals("POST") && request.getUriPath().equals("/login");
    }

    @Override
    public HttpResponse service(HttpRequest request) {
        Session session = getSession(request.getHttpCookie());
        String body = request.getBody();
        try {
            Map<String, String> formData = parseQueryParameters(body);
            User loginUser = login(formData);
            if (session == null) {
                session = new Session();
                SessionManager.getInstance().add(session);
            } else {
                SessionManager sessionManager = SessionManager.getInstance();
                sessionManager.remove(session);
                session.changeId();
                sessionManager.add(session);
            }
            session.addAttribute("user", loginUser);
        } catch (UnAuthorizedException e) {
            return HttpResponse.builder()
                .status(HttpStatus.Found)
                .header("Location", "/401.html")
                .body("")
                .build();
        }
        return HttpResponse.builder()
            .status(HttpStatus.Found)
            .header("Location", "/index.html")
            .body("")
            .cookie("JSESSIONID", session.getId())
            .build();
    }

    private Map<String, String> parseQueryParameters(String queryString) {
        Map<String, String> queryParameters = new HashMap<>();
        Arrays.stream(queryString.split("&"))
            .map(parameter -> parameter.split("="))
            .forEach(keyValue -> queryParameters.put(keyValue[0], keyValue.length == 2 ? keyValue[1] : null));
        return Collections.unmodifiableMap(queryParameters);
    }

    private Session getSession(HttpCookie httpCookie) {
        if (httpCookie == null) {
            return null;
        }
        if (httpCookie.getCookie("JSESSIONID") == null) {
            return null;
        }
        String jsessionid = httpCookie.getCookie("JSESSIONID");
        SessionManager sessionManager = SessionManager.getInstance();
        return sessionManager.findSession(jsessionid);
    }

    private User login(Map<String, String> queryParameters) {
        String account = queryParameters.get("account");
        String password = queryParameters.get("password");
        if (account == null || password == null) {
            throw new UnAuthorizedException("account or password should be not null");
        }
        Optional<User> findUser = InMemoryUserRepository.findByAccount(account);
        User user = findUser.orElseThrow(() -> new UnAuthorizedException("Invalid account " + account));
        if (!user.checkPassword(password)) {
            throw new UnAuthorizedException("Invalid password");
        }
        log.atInfo().log("user: {}", user);
        return user;
    }
}
