package org.apache.coyote.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.Manager;
import org.apache.coyote.HttpStatusCode;
import org.apache.coyote.MimeType;
import org.apache.coyote.Session;
import org.apache.coyote.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseHeader;

public class LoginController implements Controller {

    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";

    private final InMemoryUserRepository userRepository;

    public LoginController() {
        userRepository = new InMemoryUserRepository();
    }

    @Override
    public HttpResponse run(HttpRequest request) {
        if (request.isBodyEmpty()) {
            return redirectLoginPage();
        }
        String body = request.getBody();
        Map<String, String> parsedBody = parseBody(body);

        String account = parsedBody.get(ACCOUNT_KEY);
        String password = parsedBody.get(PASSWORD_KEY);

        Optional<User> optionalUser = userRepository.findByAccount(account);

        if (optionalUser.isPresent() && optionalUser.get().checkPassword(password)) {
            User user = optionalUser.get();
            ResponseHeader header = new ResponseHeader();
            Manager manager = SessionManager.getInstance();

            addSession(request, manager, user, header);
            return redirectDefaultPage(header);
        }
        return redirectUnauthorizedPage();
    }

    private HttpResponse redirectLoginPage() {
        ResponseHeader header = new ResponseHeader();
        header.setLocation("/login.html");
        header.setContentType(MimeType.HTML);
        return new HttpResponse(HttpStatusCode.FOUND, header);
    }

    private Map<String, String> parseBody(String query) {
        Map<String, String> result = new HashMap<>();
        String[] pairs = query.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            String key = keyValue[0];
            String value = keyValue[1];
            result.put(key, value);
        }
        return result;
    }

    private void addSession(HttpRequest request, Manager manager, User user, ResponseHeader header) {
        if (!isSessionExists(request)) {
            Session session = Session.createRandomSession();
            manager.add(session);
            session.setAttribute("user", user.getAccount());
            header.setCookie(HttpCookie.ofJSessionId(session.getId()));
        }
    }

    private HttpResponse redirectDefaultPage(ResponseHeader header) {
        header.setLocation("/index.html");
        header.setContentType(MimeType.HTML);
        return new HttpResponse(HttpStatusCode.FOUND, header);
    }

    private HttpResponse redirectUnauthorizedPage() {
        ResponseHeader header = new ResponseHeader();
        header.setLocation("/401.html");
        header.setContentType(MimeType.HTML);
        return new HttpResponse(HttpStatusCode.FOUND, header);
    }

    private boolean isSessionExists(HttpRequest request) {
        RequestHeader requestHeaders = request.getHeaders();
        return requestHeaders.existsSession();
    }
}
