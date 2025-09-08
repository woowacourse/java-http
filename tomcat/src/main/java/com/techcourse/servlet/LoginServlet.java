package com.techcourse.servlet;

import com.techcourse.application.LoginService;
import com.techcourse.application.dto.LoginRequest;
import com.techcourse.servlet.util.StaticFileLoader;
import java.io.IOException;
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
        String account = request.getQueryParams().get("account");
        String password = request.getQueryParams().get("password");

        try {
            loginService.login(new LoginRequest(account, password));
            response.setStatus(HttpStatus.OK);
        } catch (Exception e) {
            ServletExceptionHandler.getInstance().handle(response, e);
        }
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        try {
            byte[] content = StaticFileLoader.loadStaticFile(LOGIN_PAGE);
            response.setContentType(ContentType.getContentTypeFrom(LOGIN_PAGE));
            response.appendToBody(content);
        } catch (IOException e) {
            ServletExceptionHandler.getInstance().handle(response, e);
        }
    }
}
