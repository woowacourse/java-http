package org.apache.coyote.http11.controller.apicontroller;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.httpmessage.ContentType;
import org.apache.coyote.http11.httpmessage.request.HttpMethod;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.session.Cookie;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final Pattern LOGIN_URI_PATTERN = Pattern.compile("/login");

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return httpRequest.matchRequestLine(HttpMethod.POST, LOGIN_URI_PATTERN);
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        Map<String, Object> parameters = httpRequest.getRequestBody().getParameters();
        final String account = (String) parameters.get("account");
        final String password = (String) parameters.get("password");

        Optional<User> user = findUser(account);

        if (user.isEmpty()) {
            httpResponse.unAuthorized();
            return;
        }

        User existedUser = user.orElseThrow();

        if (!existedUser.checkPassword(password)) {
            httpResponse.unAuthorized();
            return;
        }

        log.info("로그인 성공! 아이디: " + existedUser.getAccount());
        setSession(httpRequest, existedUser);

        httpResponse.found("/index.html ")
                .addHeader("Set-Cookie", new Cookie(Map.of("JSESSIONID", httpRequest.getSession().getId())))
                .addHeader("Content-Type", ContentType.HTML.getValue() + ";charset=utf-8 ")
                .addHeader("Content-Length", "0 ");
    }

    private Optional<User> findUser(String account) {
        return InMemoryUserRepository.findByAccount(account);
    }

    private void setSession(HttpRequest httpRequest, User user) {
        SessionManager sessionManager = new SessionManager();
        Session session = httpRequest.getSession();
        session.setAttribute("user", user);
        sessionManager.add(session);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {

    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {

    }
}


