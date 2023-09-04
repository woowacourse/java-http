package nextstep.servlet.filter;

import static nextstep.jwp.controller.StaticResourceController.HOME_PAGE;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class LoginInterceptor implements Interceptor {

    @Override
    public boolean supports(HttpRequest request) {
        return request.getPath().equals("/login");
    }

    @Override
    public boolean preHandle(HttpRequest request, HttpResponse response) {
        if (request.hasCookie("JSESSIONID")) {
            final var session = request.getSession();
            if (session != null && session.getAttribute("user") != null) {
                response.sendRedirect(HOME_PAGE);
                return false;
            }
        }
        return true;
    }
}
