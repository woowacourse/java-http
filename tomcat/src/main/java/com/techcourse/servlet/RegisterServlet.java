package com.techcourse.servlet;

import com.techcourse.application.AuthService;
import com.techcourse.application.dto.RegisterRequest;
import com.techcourse.servlet.util.StaticFileLoader;
import java.io.IOException;
import java.util.Map;
import org.apache.catalina.servlet.HttpServlet;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.ContentType;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;

public class RegisterServlet extends HttpServlet {
    private static final String REGISTER_PAGE = "static/register.html";

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        try {
            byte[] content = StaticFileLoader.loadStaticFile(REGISTER_PAGE);
            response.setContentType(ContentType.fromPath(REGISTER_PAGE));
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
        String email = bodyParams.get("email");

        try {
            authService.register(new RegisterRequest(account, password, email));
            response.setStatus(HttpStatus.SEE_OTHER);
            response.addToHeader("Location", "/index.html");
        } catch (Exception e) {
            ServletExceptionHandler.getInstance().handle(response, e);
        }
    }
}
