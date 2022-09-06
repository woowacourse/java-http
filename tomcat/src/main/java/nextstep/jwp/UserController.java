package nextstep.jwp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import org.apache.coyote.http11.model.ContentType;
import org.apache.coyote.http11.model.Header;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.Resource;
import org.apache.coyote.http11.model.response.Status;

public class UserController {

    private static final UserService userService = new UserService();

    public static HttpResponse login(final HttpRequest request) throws IOException {
        if (request.getQueryParams().isEmpty()) {
            HttpResponse response = HttpResponse.of(Status.OK);
            response.addResource(findResource("/login.html"));
            return response;
        }
        try {
            userService.login(request.getQueryParams());
            HttpResponse response = HttpResponse.of(Status.FOUND);
            response.addHeader(Header.LOCATION, "/index.html");
            response.addResource(findResource("/index.html"));
            return response;
        } catch (IllegalArgumentException | NoSuchElementException e) {
            HttpResponse response = HttpResponse.of(Status.UNAUTHORIZED);
            response.addResource(findResource("/401.html"));
            return response;
        }
    }

    public static HttpResponse register(final HttpRequest request) throws IOException {
        if (!request.getBody().isBlank()) {
            RegisterRequest registerRequest = RegisterRequest.of(request.getBody());
            userService.register(registerRequest);
            HttpResponse response = HttpResponse.of(Status.FOUND);
            response.addHeader(Header.LOCATION, "/index.html");
            return response;
        }
        HttpResponse response = HttpResponse.of(Status.OK);
        response.addResource(findResource("/register.html"));
        return response;
    }

    private static Resource findResource(String url) throws IOException {
        Path path = Path.of(MainController.class.getResource("/static" + url).getPath());
        String body = Files.readString(path);

        ContentType contentType = ContentType.findByExtension(url);

        return new Resource(body, contentType);
    }
}
