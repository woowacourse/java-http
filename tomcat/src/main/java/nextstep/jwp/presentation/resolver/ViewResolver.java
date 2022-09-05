package nextstep.jwp.presentation.resolver;

import org.apache.coyote.http11.Request;
import org.apache.coyote.http11.Response;

public class ViewResolver {
    public void resolve(final Request request, final Response response) {
        if (request.getRequestURI().contains("login")) {
            response.setViewName("/login.html");
        }
    }
}
