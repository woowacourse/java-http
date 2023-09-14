package nextstep.servlet.interceptor;

import static nextstep.servlet.StaticResourceResolver.HOME_PAGE;

import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public class SessionInterceptor implements Interceptor {

    @Override
    public boolean preHandle(HttpRequest request, HttpResponse response) {
        if (request.hasCookie("JSESSIONID")) {
            final var session = request.getSession();
            if (session.getAttribute("user") != null) {
                response.sendRedirect(HOME_PAGE);
                return false;
            }
        }
        return true;
    }
}
