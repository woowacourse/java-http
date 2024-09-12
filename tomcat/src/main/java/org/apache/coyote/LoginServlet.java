package org.apache.coyote;

import com.techcourse.model.User;
import com.techcourse.service.SessionService;
import com.techcourse.service.UserService;
import org.apache.coyote.http.request.RequestBody;
import org.apache.coyote.http11.HttpCookies;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class LoginServlet extends HttpServlet {

    private final UserService userService;
    private final SessionService sessionService;

    public LoginServlet(UserService userService, SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        try {
            RequestBody requestBody = request.getBody();
            String account = requestBody.getValue("account");
            String password = requestBody.getValue("password");
            User user = userService.find(account, password);

            response.setSetCookie(sessionService.create(user));
            response.sendRedirect("/index.html");
        } catch (IllegalArgumentException exception) {
            response.sendRedirect("/401.html");
        }
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        HttpCookies cookies = request.getCookies();

        if (sessionService.isLoggedIn(cookies)) {
            response.sendRedirect("/index.html");
        } else {
            response.setStatus(HttpStatus.OK);
            response.setBody("/login.html");
        }
    }
}
