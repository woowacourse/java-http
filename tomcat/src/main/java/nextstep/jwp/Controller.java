package nextstep.jwp;

import java.util.NoSuchElementException;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.Status;

public class Controller {

    public static final String URL_LOGIN = "/login";

    private static final UserService userService = new UserService();

    public static HttpResponse process(final HttpRequest request) {
        if (isLoginRequest(request)) {
            return login(request);
        }
        return HttpResponse.of(Status.OK);
    }

    private static boolean isLoginRequest(final HttpRequest request) {
        return request.getUrl().startsWith(URL_LOGIN) && !request.getQueryParams().isEmpty();
    }

    private static HttpResponse login(final HttpRequest request) {
        try {
            userService.login(request.getQueryParams());
            HttpResponse response = HttpResponse.of(Status.FOUND);
            response.addHeader("Location", "/index.html");
            return response;
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return HttpResponse.of(Status.UNAUTHORIZED);
        }
    }
}
