package org.apache.catalina.servlet;

import com.techcourse.controller.LoginController;
import com.techcourse.service.UserService;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.requestLine.RequestLine;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.HttpResponseGenerator;
import org.apache.coyote.response.responseHeader.ContentType;
import org.apache.coyote.response.responseLine.HttpStatus;

public class LoginServlet extends HttpServlet {

    private static final String LOGIN_PATH = "/login";

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.getRequestLine();

        return requestLine.isStartsWith(LOGIN_PATH);
    }

    @Override
    public HttpResponse doGet(final HttpRequest httpRequest) {
        String requestPath = httpRequest.getRequestPath();

        String resource = findResource(LOGIN_PATH + "." + ContentType.HTML);

        String[] querys = requestPath.split("\\?")[1].split("&"); //TODO: ArgumentResolver 구현 부분
        String[] requestParams = new String[2];
        for (String query : querys) {
            final String[] split = query.split("=");

            if (split[0].equals("account")) {
                requestParams[0] = split[1];
            }
            if (split[0].equals("password")) {
                requestParams[1] = split[1];
            }
        }

        final LoginController loginController = new LoginController(new UserService()); //TODO: Bean 구현 부분
        loginController.login(requestParams[0], requestParams[1]);

        return HttpResponseGenerator.generate(resource, ContentType.HTML, HttpStatus.OK);
    }

    @Override
    public HttpResponse doPost(final HttpRequest httpRequest) {
        return HttpResponseGenerator.generate("", ContentType.HTML, HttpStatus.METHOD_NOT_ALLOWED);
    }

    private String findResource(final String requestPath) {
        URL resourceUrl = StaticResourceServlet.class.getClassLoader().getResource("static" + requestPath);

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
