package org.apache.catalina.servlet;

import static org.apache.catalina.StaticResourceUtils.getContentType;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import org.apache.catalina.HttpSession;
import org.apache.catalina.HttpSessionManager;
import org.apache.catalina.StaticResourceUtils;
import org.apache.coyote.util.HttpRequest;
import org.apache.coyote.util.HttpResponse;
import org.apache.coyote.util.RequestBody;

public class RegisterServlet extends AbstractServlet {

    private final HttpSessionManager httpSessionManager;

    public RegisterServlet(final HttpSessionManager httpSessionManager) {
        this.httpSessionManager = httpSessionManager;
    }

    @Override
    public boolean possibleHandle(final String requestPath) {
        return Objects.equals(requestPath, "/register");
    }

    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        RequestBody requestBody = httpRequest.requestBody();
        String account = requestBody.getValue("account");
        String email = requestBody.getValue("email");
        String password = requestBody.getValue("password");
        User registerUser = new User(account, email, password);
        InMemoryUserRepository.save(registerUser);

        String httpVersion = httpRequest.getHttpVersion();
        int statusCode = 302;
        String statusMessage = "Found";
        String responseBody = StaticResourceUtils.readResourceContent("/index.html");

        UUID jSessionId = UUID.randomUUID();
        httpSessionManager.add(new HttpSession(jSessionId, registerUser));

        httpResponse.putHeader("Set-Cookie", "JSESSIONID=" + jSessionId);
        httpResponse.putHeader("Content-Type", getContentType(httpRequest.getRequestPath()) + ";charset=utf-8");
        httpResponse.putHeader("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
        httpResponse.update(httpVersion, statusCode, statusMessage, responseBody);
    }

    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        String httpVersion = httpRequest.getHttpVersion();
        String responseBody = StaticResourceUtils.readResourceContent(httpRequest.getRequestPath());
        int statusCode = 200;
        String statusMessage = "OK";
        httpResponse.putHeader("Content-Type", StaticResourceUtils.getContentType(httpRequest.getRequestPath()) + ";charset=utf-8");
        httpResponse.putHeader("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
        httpResponse.update(httpVersion, statusCode, statusMessage, responseBody);
    }
}
