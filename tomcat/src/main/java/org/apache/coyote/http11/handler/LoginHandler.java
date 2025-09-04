package org.apache.coyote.http11.handler;

import com.techcourse.application.dto.LoginRequest;
import com.techcourse.controller.LoginController;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import org.apache.coyote.http11.message.HttpBody;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;

public class LoginHandler implements HttpRequestHandler {
    private final LoginController loginController = new LoginController();

    @Override
    public boolean canHandle(HttpRequest request) {
        return "/login".equals(request.getRequestPath());
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static/login.html");

        byte[] content = inputStream.readAllBytes();
        inputStream.close();

        HttpHeaders headers = HttpHeaders.fromLines(
                List.of(
                        "Content-Type: text/html;charset=utf-8",
                        "Content-Length: " + content.length
                )
        );

        login(request);

        return new HttpResponse(HttpStatus.OK, headers, HttpBody.from(content));
    }

    private void login(HttpRequest request) {
        String account = request.getQueryParams().get("account");
        String password = request.getQueryParams().get("password");
        if (Objects.isNull(account) || Objects.isNull(password)) {
            return;
        }
        loginController.login(new LoginRequest(account, password));
    }
}
