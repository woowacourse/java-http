package com.techcourse.servlet;

import com.techcourse.application.AuthService;
import com.techcourse.application.dto.LoginRequest;
import com.techcourse.exception.BusinessException;
import com.techcourse.servlet.util.StaticFileLoader;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.servlet.HttpServlet;
import org.apache.coyote.http11.message.HttpCookie;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.ContentType;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginServlet extends HttpServlet {
    private static final String LOGIN_PAGE = "static/login.html";
    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        try {
            byte[] content = StaticFileLoader.loadStaticFile(LOGIN_PAGE);
            response.setContentType(ContentType.fromPath(LOGIN_PAGE));
            response.appendToBody(content);
        } catch (IOException e) {
            ServletExceptionHandler.getInstance().handle(response, e);
        }
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> bodyParams = request.getBodyParams();
        String account = bodyParams.get("account");
        String password = bodyParams.get("password");

        try {
            authService.login(new LoginRequest(account, password));

            response.setStatus(HttpStatus.SEE_OTHER);
            addJSessionCookie(request, response);
            response.addToHeader("Location", "/index.html");

            log.info("로그인 성공! 아이디 : " + account);
        } catch (BusinessException e) {
            response.setStatus(HttpStatus.SEE_OTHER);
            response.addToHeader("Location", "/401.html");
        } catch (Exception e) {
            ServletExceptionHandler.getInstance().handle(response, e);
        }
    }

    private void addJSessionCookie(HttpRequest request, HttpResponse response) {
        if (!request.hasJSessionCookie()) {
            HttpCookie cookie = HttpCookie.of("JSESSIONID", UUID.randomUUID().toString());
            response.addToHeader("Set-Cookie", cookie.toHeaderString());
        }
    }
}
