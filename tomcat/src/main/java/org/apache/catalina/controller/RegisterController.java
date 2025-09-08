package org.apache.catalina.controller;

import static org.apache.catalina.controller.util.QueryParam.getQueryParams;
import static org.apache.catalina.controller.util.ResourceFinder.INDEX_RESOURCE_PATH;
import static org.apache.catalina.controller.util.ResourceFinder.findResource;

import com.techcourse.restController.RegisterRestController;
import com.techcourse.service.UserService;
import java.util.Map;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.requestLine.RequestLine;
import org.apache.coyote.request.requestLine.RequestPath;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.responseHeader.ContentType;
import org.apache.coyote.response.responseLine.HttpStatus;

public class RegisterController extends AbstractController {

    private static final String REGISTER_PATH = "/register";
    public static final String DOT = ".";

    public static final String ACCOUNT = "account";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.getRequestLine();

        return requestLine.isSame(REGISTER_PATH);
    }

    @Override
    public void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        RequestPath requestPath = httpRequest.getRequestPath();

        String resource = findResource(requestPath.getRequestPath() + DOT + ContentType.HTML);
        httpResponse.init(resource, ContentType.HTML, HttpStatus.OK);
    }

    @Override
    public void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final String requestBody = httpRequest.getRequestBody().getBody();
        Map<String, String> bodyValues = getQueryParams(requestBody);

        final RegisterRestController registerRestController = new RegisterRestController(
                new UserService());

        registerRestController.register(bodyValues.get(ACCOUNT), bodyValues.get(PASSWORD), bodyValues.get(EMAIL));
        httpResponse.sendRedirect(INDEX_RESOURCE_PATH);
    }
}
