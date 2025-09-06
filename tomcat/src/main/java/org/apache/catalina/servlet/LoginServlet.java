package org.apache.catalina.servlet;

import com.techcourse.controller.LoginController;
import com.techcourse.model.User;
import com.techcourse.service.UserService;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.servletContainer.session.Session;
import org.apache.catalina.servletContainer.session.SessionManager;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.requestLine.RequestLine;
import org.apache.coyote.request.requestLine.RequestPath;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.responseHeader.ContentType;
import org.apache.coyote.response.responseLine.HttpStatus;

public class LoginServlet extends HttpServlet {

    private static final String LOGIN_PATH = "/login";
    private static final String STATIC_RECOURSE_PATH = "static";
    public static final String USER = "user";

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.getRequestLine();

        return requestLine.isSame(LOGIN_PATH);
    }

    @Override
    public void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (httpRequest.hasCookie()) {
            if(isLoggedInUser(httpRequest, httpResponse))return;
        }

        RequestPath requestPath = httpRequest.getRequestPath();
        String resource = findResource(requestPath.getRequestPath() + "." + ContentType.HTML);

        httpResponse.init(resource, ContentType.HTML, HttpStatus.OK);
    }

    @Override
    public void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final String requestBody = httpRequest.getRequestBody().getBody();

        Map<String, String> bodyValues = new HashMap<>();
        String[] values = requestBody.split("&");
        for (String value : values) {
            final String[] split = value.split("=");

            bodyValues.put(split[0], split[1]);
        }

        final LoginController loginController = new LoginController(new UserService()); //TODO: Bean 구현 부분

        try {
            User user = loginController.login(bodyValues.get("account"), bodyValues.get("password"));

            setCookie(httpRequest, httpResponse, user);
            httpResponse.sendRedirect("/index.html");
        } catch (IllegalArgumentException e) { //TODO: ExceptionHandler
            httpResponse.init(findResource("/401.html"), ContentType.HTML, HttpStatus.UNAUTHORIZED);
        }
    }

    private boolean isLoggedInUser(HttpRequest httpRequest, HttpResponse httpResponse) {
        Session session = httpRequest.getSession(false);
        Optional<Object> user = session.getAttribute(USER);

        if (user.isPresent()) {
            httpResponse.init(findResource("/index.html"), ContentType.HTML, HttpStatus.FOUND);
            return true;
        }
        return false;
    }

    private void setCookie(final HttpRequest httpRequest, final HttpResponse httpResponse, final User user) {
        Session session = httpRequest.getSession(true);
        session.setAttribute(USER, user);

        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.add(session);

        httpResponse.setCookies(session.getId());
    }

    private String findResource(final String requestPath) {
        URL resourceUrl = StaticResourceServlet.class.getClassLoader().getResource(STATIC_RECOURSE_PATH + requestPath);

        try {
            Path filePath = Path.of(resourceUrl.toURI());

            return Files.readString(filePath);
        } catch (URISyntaxException | IOException e) {
            throw new IllegalArgumentException(e);
        } catch (NullPointerException e) {
            throw new NullPointerException(e.getMessage());
        }
    }
}
