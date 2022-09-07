package nextstep.jwp.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.MessageConverter;
import nextstep.jwp.service.RegisterRequest;
import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.model.ContentType;
import org.apache.coyote.http11.model.Header;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.request.Method;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.Resource;
import org.apache.coyote.http11.model.response.Status;

public class UserController {

    private static final String URL = "/login";

    private static final UserService userService = new UserService();

    public HttpResponse register(final HttpRequest request) throws IOException {
        if (request.getMethod() == Method.POST) {
            return postRegister(request);
        }
        return getRegister();
    }

    private HttpResponse getRegister() throws IOException {
        HttpResponse response = HttpResponse.of(Status.OK);
        response.addResource(findResource("/register.html"));
        return response;
    }

    private HttpResponse postRegister(final HttpRequest request) {
        RegisterRequest registerRequest = RegisterRequest.of(MessageConverter.convert(request.getBody()));
        userService.register(registerRequest);
        HttpResponse response = HttpResponse.of(Status.FOUND);
        response.addHeader(Header.LOCATION, "/index.html");
        return response;
    }

    private Resource findResource(String url) throws IOException {
        Path path = Path.of(MainController.class.getResource("/static" + url).getPath());
        String body = Files.readString(path);

        ContentType contentType = ContentType.findByExtension(url);

        return new Resource(body, contentType);
    }
}
