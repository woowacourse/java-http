package org.apache.catalina.servlet;

import com.techcourse.controller.LoginController;
import com.techcourse.service.UserService;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.requestLine.RequestLine;
import org.apache.coyote.request.requestLine.RequestPath;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.HttpResponseGenerator;
import org.apache.coyote.response.responseHeader.ContentType;
import org.apache.coyote.response.responseLine.HttpStatus;

public class LoginServlet extends HttpServlet {

    private static final String LOGIN_PATH = "/login";
    private static final String STATIC_RECOURSE_PATH = "static";

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.getRequestLine();

        return requestLine.isSame(LOGIN_PATH);
    }

    @Override
    public HttpResponse doGet(final HttpRequest httpRequest) {
        RequestPath requestPath = httpRequest.getRequestPath();

        String resource = findResource(LOGIN_PATH + "." + ContentType.HTML);

        Map<String, String> requestQueryParams = requestPath.getQueryParams();
        if(requestQueryParams.isEmpty()){
            return HttpResponseGenerator.generate(resource, ContentType.HTML, HttpStatus.OK);
        }

        final LoginController loginController = new LoginController(new UserService()); //TODO: Bean 구현 부분

        try {
            loginController.login(requestQueryParams.get("account"), requestQueryParams.get("password"));
        } catch (IllegalArgumentException e) { //TODO: ExceptionHandler
            return HttpResponseGenerator.generate(findResource("/401.html"), ContentType.HTML, HttpStatus.UNAUTHORIZED);
        }

        return HttpResponseGenerator.generate(findResource("/index.html"), ContentType.HTML, HttpStatus.FOUND);
    }

    @Override
    public HttpResponse doPost(final HttpRequest httpRequest) {
        return HttpResponseGenerator.generate("", ContentType.HTML, HttpStatus.METHOD_NOT_ALLOWED);
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
