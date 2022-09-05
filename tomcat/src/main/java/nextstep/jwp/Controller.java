package nextstep.jwp;

import org.apache.coyote.http11.model.request.Request;
import org.apache.coyote.http11.model.response.Response;
import org.apache.coyote.http11.model.response.Status;

public class Controller {

    public static final String URL_LOGIN = "/login";

    private static final UserService userService = new UserService();

    public static Response process(final Request request) {
        if (isLoginRequest(request)) {
            try {
                userService.login(request);
                Response response = Response.of(Status.FOUND);
                response.addHeader("Location", "/index.html");
                return response;
            } catch (IllegalArgumentException e) {
                return Response.of(Status.UNAUTHORIZED);
            }
        }
        return Response.of(Status.OK);
    }

    private static boolean isLoginRequest(final Request request) {
        return request.getUrl().startsWith(URL_LOGIN) && !request.getQueryParams().isEmpty();
    }
}
