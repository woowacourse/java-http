package org.apache.coyote.http11.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.session.HttpSession;
import org.apache.coyote.HttpMethodHandler;
import org.apache.coyote.common.HttpContentType;
import org.apache.coyote.common.HttpCookieResponse;
import org.apache.coyote.common.HttpMethod;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.common.QueryString;
import org.apache.coyote.util.QueryParser;
import org.apache.coyote.util.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler extends HttpMethodHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    public LoginHandler() {
        actions.put(HttpMethod.GET, this::doGet);
        actions.put(HttpMethod.POST, this::doPost);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            response.sendRedirect("/index.html");
            return;
        }
        response.setContentBody(ResourceResolver.resolve("/login.html"));
        response.setContentType(HttpContentType.TEXT_HTML);
        response.setHttpStatus(HttpStatus.OK);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String requestBody = request.getRequestBody();
        QueryString query = QueryParser.parse(requestBody);
        String account = query.get("account");
        String password = query.get("password");

        InMemoryUserRepository.findByAccount(account)
            .filter(user -> user.checkPassword(password))
            .ifPresentOrElse(user -> loginSuccess(request, response, user), () -> loginFail(response));
    }

    private void loginSuccess(HttpRequest request, HttpResponse response, User user) {
        log.info("로그인 성공! 아이디 : {}", user.getAccount());
        HttpSession session = request.getFreshSession();
        session.setAttribute("user", user);
        response.sendRedirect("/index.html");
        response.setCookie(new HttpCookieResponse("JSESSIONID", session.getId()));
    }

    private void loginFail(HttpResponse response) {
        response.sendRedirect("/401.html");
    }
}
