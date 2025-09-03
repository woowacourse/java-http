package org.apache.catalina.servlet;

import com.techcourse.controller.LoginController;
import com.techcourse.service.UserService;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.requestInfo.RequestInfo;
import org.apache.coyote.response.ContentType;
import org.apache.coyote.response.HttpResponse;

public class LoginServlet extends HttpServlet{

    private static final String LOGIN_PATH = "/login";

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        RequestInfo requestInfo = httpRequest.getRequestInfo();

        return requestInfo.isStartsWith(LOGIN_PATH);
    }

    @Override
    public HttpResponse doGet(final HttpRequest httpRequest) {
        String requestPath = httpRequest.getRequestPath();

        String resource = findResource(LOGIN_PATH + "." + ContentType.HTML);

        String[] querys = requestPath.split("\\?")[1].split("&");
        String[] requestParams = new String[2];
        for (String query : querys) {
            final String[] split = query.split("=");
            if(split[0].equals("account"))requestParams[0] = split[1];
            if(split[0].equals("password"))requestParams[1] = split[1];
        }

        final LoginController loginController = new LoginController(new UserService()); //todo: bean...
        loginController.login(requestParams[0], requestParams[1]);

        return null;
    }

    @Override
    public HttpResponse doPost(final HttpRequest httpRequest) {
        throw new IllegalArgumentException("[ERROR] 해당 요청을 찾지 못했습니다.");
    }

    private String findResource(final String requestPath){
        URL resourceUrl = StaticResourceServlet.class.getClassLoader().getResource("static"+requestPath);

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
