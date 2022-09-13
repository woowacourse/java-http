package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.service.RegisterRequest;
import nextstep.jwp.service.UserService;
import nextstep.jwp.util.MessageConverter;
import nextstep.jwp.util.ResourceLoader;
import org.apache.coyote.http11.model.Header;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.Status;

public class RegisterController implements Controller {

    private static final String URL = "/register";

    private static final UserService userService = new UserService();

    @Override
    public boolean isUrlMatches(final String url) {
        return URL.equals(url);
    }

    @Override
    public HttpResponse doGet(final HttpRequest request) throws IOException {
        HttpResponse response = HttpResponse.of(Status.OK);
        response.addResource(ResourceLoader.load("/register.html"));
        return response;
    }

    @Override
    public HttpResponse doPost(final HttpRequest request) throws IOException {
        RegisterRequest registerRequest = RegisterRequest.of(MessageConverter.convert(request.getBody()));
        userService.register(registerRequest);
        HttpResponse response = HttpResponse.of(Status.FOUND);
        response.addHeader(Header.LOCATION, "/index.html");
        return response;
    }
}
