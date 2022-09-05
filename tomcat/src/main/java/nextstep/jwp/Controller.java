package nextstep.jwp;

import java.util.NoSuchElementException;
import org.apache.coyote.http11.model.request.Request;
import org.apache.coyote.http11.model.response.Response;
import org.apache.coyote.http11.model.response.Status;

public class Controller {

    public static final String URL_LOGIN = "/login";

    private static final UserService userService = new UserService();

    public static Response process(final Request request) {
        if (isLoginRequest(request)) {
            return login(request);
        }
        return Response.of(Status.OK);
    }

    private static boolean isLoginRequest(final Request request) {
        return request.getUrl().startsWith(URL_LOGIN) && !request.getQueryParams().isEmpty();
    }

    private static Response login(final Request request) {
        try {
            userService.login(request.getQueryParams());
            Response response = Response.of(Status.FOUND);
            response.addHeader("Location", "/index.html");
            return response;
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return Response.of(Status.UNAUTHORIZED);
        }
    }
}
