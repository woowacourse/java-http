package com.techcourse.servlet;

import com.techcourse.application.LoginService;
import com.techcourse.application.dto.LoginRequest;
import com.techcourse.exception.BusinessException;
import com.techcourse.servlet.util.StaticFileLoader;
import java.io.IOException;
import java.util.Map;
import org.apache.catalina.servlet.HttpServlet;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.ContentType;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;

public class LoginServlet extends HttpServlet {
    private static final String LOGIN_PAGE = "static/login.html";

    private final LoginService loginService = new LoginService();

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> bodyParams = request.getBodyParams();
        String account = bodyParams.get("account");
        String password = bodyParams.get("password");

        try {
            loginService.login(new LoginRequest(account, password));
            response.setStatus(HttpStatus.SEE_OTHER);
            response.addToHeader("Location", "/index.html");
        } catch (BusinessException e) {
            response.setStatus(HttpStatus.SEE_OTHER);
            response.addToHeader("Location", "/401.html");
        } catch (Exception e) {
            ServletExceptionHandler.getInstance().handle(response, e);
        }
    }

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
}
