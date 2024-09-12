package com.techcourse.controller.page;

import java.io.IOException;
import org.apache.coyote.http11.Servlet;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.request.HttpServletRequest;
import org.apache.coyote.http11.response.HttpServletResponse;
import org.apache.util.FileUtils;

public class LoginPageServlet implements Servlet {

    private static final String LOGIN_SUCCESS_REDIRECT_URI = "http://localhost:8080/index.html";
    private static final String PAGE_RESOURCE_PATH = "/login.html";
    private static final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (sessionManager.hasSession(request.getSessionId())) {
            response.redirect(LOGIN_SUCCESS_REDIRECT_URI);
            return;
        }

        String fileContent = FileUtils.readFile(PAGE_RESOURCE_PATH);
        response.ok(fileContent, "html");
    }
}
