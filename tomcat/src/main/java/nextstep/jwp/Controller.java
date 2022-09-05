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

public class Controller {

    public static final String URL_LOGIN = "/login";

    private static final UserService userService = new UserService();

    public static HttpResponse process(final HttpRequest request) throws IOException {
        if (isLoginRequest(request)) {
            return login(request);
        }
        HttpResponse response = HttpResponse.of(Status.OK);
        response.addResource(findResource(request.getUrl()));
        return response;
    }

    private static boolean isLoginRequest(final HttpRequest request) {
        return request.getUrl().startsWith(URL_LOGIN) && !request.getQueryParams().isEmpty();
    }

    private static HttpResponse login(final HttpRequest request) throws IOException {
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

    private static Resource findResource(String url) throws IOException {
        if (url.equals("/")) {
            url += "hello.txt";
        }
        Path path = Path.of(Controller.class.getResource("/static" + url).getPath());
        String body = Files.readString(path);

        ContentType contentType = ContentType.findByExtension(url);

        return new Resource(body, contentType);
    }
}
