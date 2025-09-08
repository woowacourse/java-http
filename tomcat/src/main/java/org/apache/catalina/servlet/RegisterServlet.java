package org.apache.catalina.servlet;

import com.techcourse.controller.RegisterController;
import com.techcourse.service.UserService;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.requestLine.RequestLine;
import org.apache.coyote.request.requestLine.RequestPath;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.responseHeader.ContentType;
import org.apache.coyote.response.responseLine.HttpStatus;

public class RegisterServlet extends HttpServlet {

    private static final String REGISTER_PATH = "/register";
    private static final String STATIC_RECOURSE_PATH = "static";

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.getRequestLine();

        return requestLine.isSame(REGISTER_PATH);
    }

    @Override
    public void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
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

        final RegisterController registerController = new RegisterController(new UserService());

        registerController.register(bodyValues.get("account"), bodyValues.get("password"), bodyValues.get("email"));
        httpResponse.init(findResource("/index.html"), ContentType.HTML, HttpStatus.FOUND);
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
